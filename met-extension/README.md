Masked input For MaterialEditText
============

Masked input for [MaterialEditText](https://github.com/rengwuxian/MaterialEditText)


How to Use
----------

Add MaskedMETEditText to your layout

    <com.shaubert.maskedinput.metextension.MaskedMETEditText
        android:id="@+id/masked_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:mi_mask="+7 ### ##-##-##"/>
        
Set the mask in code

    maskedEditText = (MaskedMETEditText) findViewById(R.id.masked_input);
    maskedEditText.setMask("+7 ### ##-##-##");
    

How to add to your project
--------------------------

Add dependency to your pom file:

    repositories {
        maven{url "https://github.com/shaubert/maven-repo/raw/master/releases"}
    }
    dependencies {
        compile 'com.shaubert.maskedinput:met-extension:1.2'
    }
