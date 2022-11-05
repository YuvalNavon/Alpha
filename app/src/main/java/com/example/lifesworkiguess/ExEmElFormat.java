package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ExEmElFormat extends AppCompatActivity {

EditText nameET, ingredientsET, stepsET;
String name, ingredients, steps;
StorageReference fStorage, fRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_em_el_format);

        nameET = (EditText) findViewById(R.id.dishName);
        ingredientsET = findViewById(R.id.Ingredients);
        stepsET = findViewById(R.id.Steps);

        fStorage = FirebaseStorage.getInstance().getReference();




    }



    public void makeXML(View view) throws ParserConfigurationException, TransformerException {
        name = nameET.getText().toString();
        ingredients = ingredientsET.getText().toString();
        steps = stepsET.getText().toString();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Recipe");
        doc.appendChild(rootElement);
        Element nameE = doc.createElement("Title");
        Attr actualName = doc.createAttribute("Recipe_Title");
        actualName.setValue(name);
        nameE.setAttributeNode(actualName);
        rootElement.appendChild(nameE);

        Element ingredientsE = doc.createElement("Ingredients");
        nameE.appendChild(ingredientsE);
        Attr actualIngredients = doc.createAttribute("Actual_Ingredients");
        actualIngredients.setValue(ingredients);
        ingredientsE.setAttributeNode(actualIngredients);

        Element stepsE = doc.createElement("Steps");
        nameE.appendChild(stepsE);
        Attr actualSteps = doc.createAttribute("Actual_Steps");
        actualSteps.setValue(steps);
        stepsE.setAttributeNode(actualSteps);



        //...create XML elements, and others...

        // write dom document to a file



        try (FileOutputStream output =
                     new FileOutputStream(this.getFilesDir().getPath() + "/recipe2.xml")) {
           writeXml(doc, output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadXML();



    }

    public void writeXml(Document doc,
                          OutputStream output)
            throws TransformerException {

        Toast.makeText(this, "FILE CREATED", Toast.LENGTH_LONG).show();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);


    }



    public String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void uploadXML(){
        String XMLFilePath = this.getFilesDir().getPath() + "/recipe2.xml";
        File XMLFile = new File(XMLFilePath);
        Uri XMLUri = Uri.fromFile(XMLFile);
        if (XMLUri!=null){

            fRef = fStorage.child(System.currentTimeMillis() + "." + getFileExtension(XMLUri));
            fRef.putFile(XMLUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ExEmElFormat.this, "Recipe Uploaded!", Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            Toast.makeText(this, "Error, File was not selected", Toast.LENGTH_LONG).show();
        }
    }

    public void clear (View view){
        nameET.setText("");
        ingredientsET.setText("");
        stepsET.setText("");
    }

    public void getXML(View view){

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.XMLScreen);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getTitle().toString().equals("User Authentication") ){
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Gallary Choose") ){
            Intent si = new Intent(this, GallaryChoose.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Camera") ){
            Intent si = new Intent(this, CameraUpload.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Notification") ){
            Intent si = new Intent(this, NotificationScreen.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Time Picker") ){
            Intent si = new Intent(this, TimePickerToast.class);
            startActivity(si);
        }


        return true;
    }

}