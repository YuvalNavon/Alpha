package com.example.lifesworkiguess;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
String name, ingredients, steps, lastUploaded, filesDir, lastName, lastIng, lastSteps;
StorageReference fStorage, fRef, fDownRef;
boolean cleared, downloading;
FileInputStream fis;
InputStreamReader isr;
BufferedReader br;
StringBuffer sb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_em_el_format);

        nameET = (EditText) findViewById(R.id.dishName);
        ingredientsET = findViewById(R.id.Ingredients);
        stepsET = findViewById(R.id.Steps);

        fStorage = FirebaseStorage.getInstance().getReference();
        filesDir = this.getFilesDir().getPath();

        cleared = false;
        downloading = false;

    }



    public void makeXML(View view) throws ParserConfigurationException, TransformerException {
        name = nameET.getText().toString();
        ingredients = ingredientsET.getText().toString();
        steps = stepsET.getText().toString();
        if (!name.equals("")&& !ingredients.equals("") && !steps.equals("")){
            cleared = false;
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
            rootElement.appendChild(ingredientsE);
            Attr actualIngredients = doc.createAttribute("Actual_Ingredients");
            actualIngredients.setValue(ingredients);
            ingredientsE.setAttributeNode(actualIngredients);

            Element stepsE = doc.createElement("Steps");
            rootElement.appendChild(stepsE);
            Attr actualSteps = doc.createAttribute("Actual_Steps");
            actualSteps.setValue(steps);
            stepsE.setAttributeNode(actualSteps);



            //...create XML elements, and others...

            // write dom document to a file



            try (FileOutputStream output =
                         new FileOutputStream(this.getFilesDir().getPath() + "/recipe3.xml")) {
                writeXml(doc, output);
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadXML();

        }

        else{
            Toast.makeText(ExEmElFormat.this, "Please fill ALL Fields!", Toast.LENGTH_LONG).show();

        }



    }

    public void writeXml(Document doc,
                          OutputStream output)
            throws TransformerException {


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);


    }





    public void uploadXML(){
        String XMLFilePath = filesDir + "/recipe3.xml";
        File XMLFile = new File(XMLFilePath);
        Uri XMLUri = Uri.fromFile(XMLFile);
        if (XMLUri!=null){

            fRef = fStorage.child("Recipe For " + name + "." + "xml");

            fRef.putFile(XMLUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ExEmElFormat.this, "Recipe Uploaded!", Toast.LENGTH_LONG).show();
                    lastUploaded = "Recipe For " + name + "." + "xml";
                    lastName = name;
                    lastIng = ingredients;
                    lastSteps = steps;
                    downloading = false;
                }
            });
        }
        else
        {
            Toast.makeText(this, "Error, File was not Uploaded", Toast.LENGTH_LONG).show();
        }
    }

    public void clear (View view){
        nameET.setText("");
        ingredientsET.setText("");
        stepsET.setText("");
        cleared = true;
    }

    public void getXML(View view){
        if (lastUploaded!=null ){
            if (cleared){

                fDownRef = fStorage.child(lastUploaded);
                fDownRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String strUrl = uri.toString();
                        Toast.makeText(ExEmElFormat.this, "Downloading...", Toast.LENGTH_LONG).show();
                        downloadFiles(ExEmElFormat.this, "RecipeDown", ".xml","MyDir" , strUrl);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ExEmElFormat.this, "Recipe Not Found!", Toast.LENGTH_LONG).show();
                    }
                });


            }
            else{
                Toast.makeText(ExEmElFormat.this, "Press The CLEAR Button First!", Toast.LENGTH_LONG).show();

            }

        }
        else{
            Toast.makeText(ExEmElFormat.this, "No Recipe Available!", Toast.LENGTH_LONG).show();

        }





    }

    public void downloadFiles(Context context, String fileName, String fileExtenstion, String destinationDirectory, String url){
        if (isFileExists(fileName+fileExtenstion))
        {
            deleteFile(fileName+fileExtenstion);
        }

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtenstion);

        downloadManager.enqueue(request);
        downloading = true;
    }

    public void readXML(View view) {
        if (downloading) {
            if (isFileExists("RecipeDown.xml")) {
                File file = new File(this.getExternalFilesDir("MyDir"), "RecipeDown.xml");
                try {
                    fis = new FileInputStream(file);
                    XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                    parser.setInput(fis, null);
                    int event = parser.getEventType();
                    while (event != XmlPullParser.END_DOCUMENT) {
                        if (event == XmlPullParser.START_TAG) {

                            System.out.println("TAGS: " + parser.getName());
                            if (parser.getAttributeCount()== 1){
                                switch (parser.getAttributeName(0)){
                                    case "Recipe_Title":
                                        nameET.setText(parser.getAttributeValue(0));
                                    case "Actual_Ingredients":
                                        ingredientsET.setText(parser.getAttributeValue(0));
                                    case "Actual_Steps":
                                        stepsET.setText(parser.getAttributeValue(0));
                                }

                            }

                        }
                        event = parser.next();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                Toast.makeText(this, "Wait For the Recipe to be Downloaded!", Toast.LENGTH_SHORT).show();
            }



        }
        else{
            Toast.makeText(this, "Please Download the Recipe First!", Toast.LENGTH_SHORT).show();

        }
    }






    public boolean isFileExists(String filename){


        File folder1 = new File(this.getExternalFilesDir("MyDir"), filename);
        return folder1.exists();

    }

    public boolean deleteFile( String filename) {

        File folder1 = new File(this.getExternalFilesDir("MyDir"), filename);
        return folder1.delete();
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



        return true;
    }

}