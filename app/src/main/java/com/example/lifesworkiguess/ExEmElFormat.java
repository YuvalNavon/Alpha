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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
String name, ingredients, steps, lastUploaded, filesDir, lastName, lastIng, lastSteps;
StorageReference fStorage, fRef, fDownRef;
boolean cleared;
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
                        downloadFiles(ExEmElFormat.this, "RecipeDown", ".xml", filesDir, strUrl);
                        readXML();


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
        if (isFileExists(fileName+fileExtenstion)){
            System.out.println("FILE: EXISTS ") ;
            deleteFile(fileName+fileExtenstion);
        }
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtenstion);

        downloadManager.enqueue(request);
    }

    public void readXML(){
        try {
            fis= openFileInput("recipe3.xml");
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            sb = new StringBuffer();
            String line = br.readLine();
            String[] lines = new String[3];
            int i = 0;
            while (i<3) {
                sb.append(line+'\n');
                line = br.readLine();
                lines[i] = line;
                i++;
            }
            String new_name = lines[0].substring(lines[0].indexOf('=')+2, lines[0].indexOf('>')-1);
            String new_ing = lines[1].substring(lines[1].indexOf('=')+2, lines[1].indexOf('>')-2);
            String new_steps = lines[2].substring(lines[2].indexOf('=')+2, lines[2].indexOf('>')-2);

            nameET.setText(new_name);
            ingredientsET.setText(new_ing);
            stepsET.setText(new_steps);







        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        File xmlf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "RecipeDown.xml");

    }

    public boolean isFileExists(String filename){

        File folder1 = new File( "/data/com.example.lifesworkiguess/files/" + filename);
        System.out.println("FILE: " + folder1.exists());
        System.out.println("/data/data/com.example.lifesworkiguess/files/" + filename);
        return folder1.exists();

    }

    public boolean deleteFile( String filename) {

        File folder1 = new File("/data/data/com.example.lifesworkiguess/files/" + filename);
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
        if (item.getTitle().toString().equals("Time Picker") ){
            Intent si = new Intent(this, TimePickerToast.class);
            startActivity(si);
        }


        return true;
    }

}