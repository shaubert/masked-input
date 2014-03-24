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

How to add to your project
--------------------------

Add dependency to your pom file:

    <dependency>
        <groupId>com.shaubert.maskedinput</groupId>
        <artifactId>masked-input</artifactId>
        <version>1.0.2</version>
    </dependency>

and repository:

    <repositories>
        <repository>
            <id>git.shaubert.repo</id>
            <url>https://github.com/shaubert/maven-repo/raw/master/releases</url>
        </repository>
    </repositories>
