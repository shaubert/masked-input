Masked input
============

Masked input for Android

How to Use
----------

Add MaskedEditText to your layout

    <com.shaubert.masked.input.MaskedEditText
        android:id="@+id/masked_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
        
Set the mask

    maskedEditText = (MaskedEditText) findViewById(R.id.masked_input);
    maskedEditText.setMask("+7 ### ##-##-##");
    
By default MaskedEditText supports only digits mask character (#). But it's easy to extend. 
You need to implement `MaskChar` interface. For example lets look at `NumericChar`:

    public class NumericChar implements MaskChar {
        @Override
        public char getMaskChar() {
            return '#';
        }
        
       @Override
        public boolean isValid(char replacement) {
            return Character.isDigit(replacement);
        }
        
        @Override
        public int getInputTypeClass() {
            return InputType.TYPE_CLASS_NUMBER;
        }
    }
    
From `getInputTypeClass` you can return only one of:
* `InputType.TYPE_CLASS_TEXT` 
* `InputType.TYPE_CLASS_PHONE` 
* `InputType.TYPE_CLASS_NUMBER`
* `InputType.TYPE_CLASS_DATETIME`

You can add custom `MaskChar` implementations with `MaskedEditText.addMaskChar()` call.

Also you could change the default mask character replacement (`'_'`) with 
`MaskedEditText.setMaskCharReplacement()` method. But remember, user is unable to input replacement char.
