package com.shaubert.masked.input;

import android.text.*;
import android.view.inputmethod.EditorInfo;

public class MaskedInputFormatterTextWatcher implements TextWatcher {

    public static final String TAG = MaskedInputFormatterTextWatcher.class.getSimpleName();


    private boolean selfChange;
    private String mask;
    private int firstInputStart;
    private int lastInputEnd;
    private MaskChar[] maskCharsArr;

    private int newCharsStart;
    private int newCharsCount;
    private int selectionBeforeChange;
    private int editablePlacesBeforeCursor;
    private boolean deletionOfMultipleChars;
    private final MaskCharsMap maskCharsMap;
    private final char maskCharReplacement;

    public MaskedInputFormatterTextWatcher(MaskCharsMap maskCharsMap, char maskCharReplacement) {
        this.maskCharsMap = maskCharsMap;
        this.maskCharReplacement = maskCharReplacement;
    }

    public void setMask(String mask) {
        this.mask = mask;
        if (mask == null) {
            maskCharsArr = null;
        } else {
            maskCharsArr = new MaskChar[mask.length()];
            firstInputStart = -1;
            lastInputEnd = -1;
            for (int i = 0; i < mask.length(); i++) {
                char ch = mask.charAt(i);
                maskCharsArr[i] = maskCharsMap.getMaskChar(ch);
                if (maskCharsArr[i] != null) {
                    if (firstInputStart == -1) {
                        firstInputStart = i;
                    }
                    lastInputEnd = i;
                }
            }
        }
    }

    public String getMask() {
        return mask;
    }

    public boolean isSelfChange() {
        return selfChange;
    }

    public int getInputType() {
        if (mask == null) {
            return InputType.TYPE_CLASS_TEXT;
        }

        boolean hasText = false;
        boolean hasDateTime = false;
        boolean hasNumber = false;
        boolean hasPhone = false;
        for (MaskChar maskChar : maskCharsArr) {
            if (maskChar != null) {
                int maskCharInputType = maskChar.getInputTypeClass();
                final int cls = maskCharInputType & EditorInfo.TYPE_MASK_CLASS;
                if (cls == EditorInfo.TYPE_CLASS_TEXT) {
                    hasText = true;
                } else if (cls == EditorInfo.TYPE_CLASS_NUMBER) {
                    hasNumber = true;
                } else if (cls == EditorInfo.TYPE_CLASS_DATETIME) {
                    hasDateTime = true;
                } else if (cls == EditorInfo.TYPE_CLASS_PHONE) {
                    hasPhone = true;
                } else {
                    hasText = true;
                }
            }
        }

        if (hasText) {
            return InputType.TYPE_CLASS_TEXT;
        } else if (hasDateTime) {
            return InputType.TYPE_CLASS_DATETIME;
        } else if (hasPhone) {
            return InputType.TYPE_CLASS_PHONE;
        } else if (hasNumber) {
            return InputType.TYPE_CLASS_NUMBER;
        } else {
            return InputType.TYPE_CLASS_TEXT;
        }
    }

    public SpannableStringBuilder getFormattedMask() {
        if (mask == null) {
            return null;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int inputGroupStart = 0;
        for (int i = 0; i < mask.length(); i++) {
            char ch = mask.charAt(i);
            boolean prevMaskChar = i > 0 && builder.charAt(i - 1) == maskCharReplacement;
            MaskChar maskChar = maskCharsArr[i];
            if (maskChar != null) {
                builder.append(maskCharReplacement);
                if (!prevMaskChar) {
                    inputGroupStart = i;
                }
            } else {
                builder.append(ch);
                if (prevMaskChar) {
                    MaskChar[] masks = new MaskChar[i - inputGroupStart];
                    System.arraycopy(maskCharsArr, inputGroupStart, masks, 0, masks.length);
                    builder.setSpan(new InputGroup(masks.length, masks), inputGroupStart, i, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    inputGroupStart = -1;
                }
            }
        }
        if (inputGroupStart >= 0) {
            MaskChar[] masks = new MaskChar[mask.length() - inputGroupStart];
            System.arraycopy(maskCharsArr, inputGroupStart, masks, 0, masks.length);
            builder.setSpan(new InputGroup(masks.length, masks),
                    inputGroupStart, builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return builder;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (selfChange || mask == null) {
            return;
        }

        selectionBeforeChange = Selection.getSelectionStart(s);
        editablePlacesBeforeCursor = 0;
        int len = Math.min(Math.min(s.length(), mask.length()), selectionBeforeChange);
        for (int i = 0; i < len; i++) {
            if (maskCharsArr[i] != null) {
                editablePlacesBeforeCursor++;
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (selfChange) {
            return;
        }

        newCharsStart = start;
        newCharsCount = count;
        deletionOfMultipleChars = false;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (selfChange || mask == null) {
            return;
        }

        if (newCharsCount > 0) {
            if (newCharsCount > 1) {
                String newText = editable.toString().substring(newCharsStart, newCharsStart + newCharsCount);
                CharSequence repl = removeFormatting(newText, newCharsStart);
                selfChange = true;
                editable.replace(newCharsStart, newCharsStart + newCharsCount, repl);
                newCharsCount = repl.length();
                selfChange = false;
            }
            editablePlacesBeforeCursor += newCharsCount;
        } else {
            int selectionStart = Selection.getSelectionStart(editable);
            deletionOfMultipleChars = Math.abs(selectionBeforeChange - selectionStart) > 1;
            if (deletionOfMultipleChars) {
                editablePlacesBeforeCursor = 0;
                int len = Math.min(Math.min(editable.length(), mask.length()), selectionStart);
                for (int i = 0; i < len; i++) {
                    if (maskCharsArr[i] != null) {
                        editablePlacesBeforeCursor++;
                    }
                }
            } else {
                editablePlacesBeforeCursor--;
            }
        }

        String unmaskedValue = getUnmaskedValueAfterInput(editable);
        SpannableStringBuilder newText = getFormattedMask();
        int counter = 0;
        int newSelectionStart = -1;
        for (int i = 0; i < newText.length() && counter < unmaskedValue.length(); i++) {
            char source = newText.charAt(i);
            if (source == maskCharReplacement) {
                if (newSelectionStart == -1) {
                    newSelectionStart = i;
                }
                char value = unmaskedValue.charAt(counter);
                if (editablePlacesBeforeCursor >= 0) {
                    if (editablePlacesBeforeCursor == 0) {
                        if (newCharsCount != 1
                                || counter == 0
                                || unmaskedValue.charAt(counter - 1) != maskCharReplacement) {
                            newSelectionStart = i;
                        }
                    } else {
                        newSelectionStart = i;
                    }
                    editablePlacesBeforeCursor--;
                }
                newText.replace(i, i + 1, String.valueOf(value));
                counter++;

                if (counter == unmaskedValue.length()) {
                    if (editablePlacesBeforeCursor >= 0) {
                        newSelectionStart = i + 1;
                    }
                    break;
                }
            }
        }
        if (newSelectionStart == -1) {
            newSelectionStart = Selection.getSelectionStart(editable);
        }

        selfChange = true;
        InputGroup[] spans = editable.getSpans(0, editable.length(), InputGroup.class);
        for (InputGroup inputGroup : spans) {
            editable.removeSpan(inputGroup);
        }
        editable.replace(0, editable.length(), newText, 0, newText.length());
        Selection.setSelection(editable, newSelectionStart);
        selfChange = false;
    }

    private String getUnmaskedValueAfterInput(Editable editable) {
        String text = editable.toString();
        StringBuilder result = new StringBuilder();
        InputGroup[] inputGroups = editable.getSpans(0, editable.length(), InputGroup.class);
        boolean newCharsWasHandled = false;
        String charsFromLastGroup = null;
        int lastGroupEnd = -1;
        for (int i = 0; i < inputGroups.length; i++) {
            InputGroup group = inputGroups[i];
            int start = editable.getSpanStart(group);
            if (start < lastGroupEnd) {
                start = lastGroupEnd;
            }
            int end = editable.getSpanEnd(group);
            if (end < lastGroupEnd) {
                end = lastGroupEnd;
            }
            StringBuilder groupText = new StringBuilder(editable.subSequence(start, end));

            if (!newCharsWasHandled && newCharsStart < start) {
                charsFromLastGroup = text.substring(newCharsStart, newCharsStart + newCharsCount);
                newCharsWasHandled = true;
            }

            if (!TextUtils.isEmpty(charsFromLastGroup)) {
                int count = Math.min(group.length, charsFromLastGroup.length());
                replaceTextAccordingToMask(groupText, 0, count, charsFromLastGroup, group);
                if (count == charsFromLastGroup.length()) {
                    charsFromLastGroup = null;
                } else {
                    charsFromLastGroup = charsFromLastGroup.substring(count);
                }
            } else if (!newCharsWasHandled && newCharsStart >= start && newCharsStart <= end) {
                int newCharsLocalPos = newCharsStart - start;
                if (newCharsCount > 0) {
                    groupText.delete(newCharsLocalPos, newCharsLocalPos + newCharsCount);
                    int count = newCharsCount;
                    if (newCharsLocalPos + newCharsCount > group.length) {
                        count = group.length - newCharsLocalPos;
                        charsFromLastGroup = text.substring(newCharsStart + count, newCharsStart + newCharsCount);
                    }
                    if (count > 0) {
                        replaceTextAccordingToMask(groupText, newCharsLocalPos, newCharsLocalPos + count,
                                text.substring(newCharsStart, newCharsStart + count), group);
                    }
                } else {
                    if (groupText.length() < group.length) {
                        while (groupText.length() < group.length) {
                            groupText.insert(newCharsLocalPos, maskCharReplacement);
                        }
                    } else if (newCharsStart == end && !deletionOfMultipleChars) {
                        groupText.replace(newCharsLocalPos - 1, newCharsLocalPos, String.valueOf(maskCharReplacement));
                    }
                }
                newCharsWasHandled = true;
            } else if (newCharsStart > end && newCharsCount == 0) {
                if (!deletionOfMultipleChars) {
                    InputGroup nextGroup = (i + 1) < inputGroups.length ? inputGroups[i + 1] : null;
                    int nextStart = nextGroup != null ? editable.getSpanStart(nextGroup) : 0;
                    int nextEnd = nextGroup != null ? editable.getSpanEnd(nextGroup) : 0;
                    if (nextGroup == null || (nextStart == newCharsStart && nextEnd - nextStart == nextGroup.length)) {
                        groupText.replace(group.length - 1, group.length, String.valueOf(maskCharReplacement));
                    }
                }
            }
            while (groupText.length() < group.length) {
                groupText.insert(groupText.length(), maskCharReplacement);
            }
            lastGroupEnd = end;
            result.append(groupText);
        }
        return result.toString();
    }

    private void replaceTextAccordingToMask(StringBuilder groupText, int start, int end, String replacement, InputGroup group) {
        int count = end - start;
        if (count < 0 || count > replacement.length() || groupText.length() > group.length) {
            throw new IllegalArgumentException("wrong arguments for replacement, groupText=" + groupText
                    + ", start=" + start + ", end=" + end + ", replacement=" + replacement + ", groupLen=" + group.length);
        }
        for (int i = 0; i < count; i++) {
            char newChar = replacement.charAt(i);
            if (group.maskChars[i].isValid(newChar)) {
                groupText.replace(start + i, start + i + 1, String.valueOf(newChar));
            }
        }
    }

    public int getMaskedCharsCount() {
        if (mask == null) {
            return 0;
        }
        int count = 0;
        for (MaskChar maskChar : maskCharsArr) {
            if (maskChar != null) {
                count++;
            }
        }
        return count;
    }

    public int getLastAllowedSelectionPosition(CharSequence s) {
        if (s == null) {
            return 0;
        }
        if (mask == null) {
            return s.length();
        }

        int min = Math.min(s.length(), mask.length());
        int lastValuePos = -1;
        boolean moveToNextReplChar = false;
        for (int i = 0; i < min; i++) {
            char ch = s.charAt(i);
            if (ch != mask.charAt(i)) {
                if (ch != maskCharReplacement) {
                    lastValuePos = i + 1;
                    moveToNextReplChar = true;
                } else {
                    moveToNextReplChar = false;
                    if (lastValuePos == -1) {
                        lastValuePos = i;
                    }
                }
            } else if (moveToNextReplChar) {
                lastValuePos++;
            }
        }
        if (lastValuePos == -1) {
            lastValuePos = 0;
        }
        return lastValuePos;
    }

    public int getNextSelectionPosition(int cur) {
        if (mask == null || firstInputStart == -1) {
            return cur;
        }

        if (cur > lastInputEnd + 1) {
            return lastInputEnd + 1;
        } else if (cur <= firstInputStart) {
            return firstInputStart;
        } else {
            if (maskCharsArr[cur - 1] != null) {
                return cur;
            }
            for (; cur < lastInputEnd; cur++) {
                if (maskCharsArr[cur] != null) {
                    return cur;
                }
            }
            return cur;
        }
    }

    public String getUnmaskedValue(String s) {
        if (s == null) {
            return null;
        }
        if (mask == null) {
            return s;
        }

        StringBuilder builder = new StringBuilder();
        int min = Math.min(s.length(), mask.length());
        for (int i = 0; i < min; i++) {
            char ch = s.charAt(i);
            if (ch != mask.charAt(i) && ch != maskCharReplacement) {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    public CharSequence getFormattedValue(String text) {
        if (mask == null) {
            return text;
        }
        if (text == null) {
            return getFormattedMask();
        }

        CharSequence value = removeFormatting(text, 0);
        SpannableStringBuilder newText = getFormattedMask();
        int counter = 0;
        for (int i = 0; i < newText.length() && counter < value.length(); i++) {
            char source = newText.charAt(i);
            if (source == maskCharReplacement) {
                newText.replace(i, i + 1, value, counter, counter + 1);
                counter++;
            }
        }
        return newText;
    }

    private CharSequence removeFormatting(CharSequence text, int maskOffset) {
        if (text == null) {
            return null;
        }
        if (mask == null) {
            return text;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        int pos = 0;
        while (pos < builder.length() && maskOffset < mask.length()) {
            MaskChar maskChar = maskCharsArr[maskOffset];
            if (maskChar != null) {
                char ch = builder.charAt(pos);
                if (maskChar.isValid(ch)) {
                    maskOffset++;
                    pos++;
                } else {
                    builder.delete(pos, pos + 1);
                }
            } else {
                maskOffset++;
            }
        }
        return builder.toString();
    }

    private static class InputGroup {
        final int length;
        final MaskChar[] maskChars;

        private InputGroup(int length, MaskChar[] maskChars) {
            this.length = length;
            this.maskChars = maskChars;
        }
    }

}