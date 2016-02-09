Masked input
============

Masked input for Android

![Sample App Screenshot](../master/screenshots/sample.png?raw=true)

How to Use
----------

Add MaskedEditText to your layout

    <com.shaubert.maskedinput.MaskedEditText
        android:id="@+id/masked_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:mi_mask="+7 ### ##-##-##"/>
        
Set the mask in code

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
        
        ...
        
    }
    
From `getInputTypeClass` you can return only one of:
* `InputType.TYPE_CLASS_TEXT` 
* `InputType.TYPE_CLASS_PHONE` 
* `InputType.TYPE_CLASS_NUMBER`
* `InputType.TYPE_CLASS_DATETIME`

Also you are able to define custom spans for placeholder and for entered value in mask with

    @Override
    public Object getSpanForPlaceholder() {
        if (maskColor != null) {
            return new ForegroundColorSpan(maskColor);
        }

        return null;
    }

    @Override
    public Object getSpanForValue() {
        if (valueColor != null) {
            return new ForegroundColorSpan(valueColor);
        }

        return null;
    }

You can pass custom `MaskChar` implementations to `MasMaskedEditText` with `addMaskChar()` call.

Also you could change the default mask character replacement (`'_'`) with 
`MaskedEditText.setMaskCharReplacement()` method. But remember, user is unable to input replacement char.

List of supported xml attributes:
* `mi_mask`, format="string|reference" — mask (for example "####-####-####-####")
* `mi_placeholder`, format="string|reference" — placeholder (for example "X")

How to add to your project
--------------------------

Add dependency to your pom file:

    repositories {
        maven{url "https://github.com/shaubert/maven-repo/raw/master/releases"}
    }
    dependencies {
        compile 'com.shaubert.maskedinput:library:1.2.1'
    }
