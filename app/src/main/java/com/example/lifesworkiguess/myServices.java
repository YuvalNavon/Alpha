package com.example.lifesworkiguess;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class myServices {


    //SIGN UP INFO VALIDATION

    public static void isEmailAvailable(String email, OnEmailCheckListener listener) {
        FirebaseDatabase FDBD = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference refUsers = FDBD.getReference("Users");
        Query query = refUsers.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAvailable = !snapshot.exists();
                listener.onEmailCheck(isAvailable);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public static boolean emailInFormat(String email){
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static boolean passwordValid(String password){
        return password.length()>6;
    }

    public static void isUsernameAvailable(String username, OnUsernameCheckListener listener) {
        FirebaseDatabase FDBD = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference refUsers = FDBD.getReference("Users");

        Query query = refUsers.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isAvailable = !dataSnapshot.exists();
                listener.onUsernameCheck(isAvailable);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onUsernameCheck(false);
            }
        });
    }


    //RECIPE, XML, DOWNLOADS
    public static Recipe XMLToRecipe(Context context, String fileName){
        String recipeName = "ERROR";
        Recipe recipe = new Recipe(recipeName);
        ArrayList<Ingredient> ingsList = new ArrayList<>();
        ArrayList<Step> stepsList = new ArrayList<>();
        if (isFileExists(context, fileName)) {
            File file = new File(context.getFilesDir(), fileName);
            try {
                FileInputStream fis = new FileInputStream(file);
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(fis, null);
                int event = parser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    if (event == XmlPullParser.START_TAG) {

                        if (parser.getName().equals("General")){
                            recipeName = parser.getAttributeValue(0);
                            recipe.setTitle(recipeName);


                            //PLACEHOLDER FOR IF I EVER NEED TO BRING THESE PROPERTIES BACK TO RECIPE AND NOT LESSON
//                            String time = parser.getAttributeValue(1);
//                            String difficulty = parser.getAttributeValue(2);
//                            boolean kosher = Boolean.parseBoolean(parser.getAttributeValue(3));
//                            int serveCount = Integer.parseInt(parser.getAttributeValue(4));
//                            recipe.setTime(time);
//                            recipe.setDifficulty(difficulty);
//                            recipe.setKosher(kosher);
//                            recipe.setServeCount(serveCount);

                        }
                        if (parser.getName().equals("Ingredient")){
                            Ingredient ingredient = new Ingredient(parser.getAttributeValue(0),
                                    parser.getAttributeValue(1), parser.getAttributeValue(2));
                            ingsList.add(ingredient);


                        }
                        if (parser.getName().equals("Step")){


                            Step step = new Step(parser.getAttributeValue(0), parser.getAttributeValue(1),
                                    parser.getAttributeValue(2), parser.getAttributeValue(null, "Action"));
                            stepsList.add(step);

                        }


                    }
                    event = parser.next();
                }
                for (int i=0; i<ingsList.size();i++){
                    recipe.addIngredient(ingsList.get(i));

                }
                for (int i=0; i<stepsList.size();i++){
                    recipe.addStep(stepsList.get(i));

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return recipe;
    }


    public static void recipeToXML(Context context, Recipe recipe, String fileName){
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Recipe");
        doc.appendChild(rootElement);

        //GENERAL TAG - Recipe Title, Time, Difficulty, Kosher and Serve Count

        Element GeneralE = doc.createElement("General");
        Attr recipeTitle = doc.createAttribute("Title");
        recipeTitle.setValue(recipe.getTitle());
        GeneralE.setAttributeNode(recipeTitle);

        //PLACEHOLDER FOR IF I EVER NEED TO BRING THESE PROPERTIES BACK TO RECIPE AND NOT LESSON

//        Attr recipeTime = doc.createAttribute("Time");
//        recipeTime.setValue(recipe.getTime());
//        GeneralE.setAttributeNode(recipeTime);
//
//        Attr recipeDifficulty = doc.createAttribute("Difficulty");
//        recipeDifficulty.setValue(recipe.getDifficulty());
//        GeneralE.setAttributeNode(recipeDifficulty);
//
//        Attr recipeKosher = doc.createAttribute("Kosher");
//        recipeKosher.setValue(Boolean.toString(recipe.isKosher()));
//        GeneralE.setAttributeNode(recipeKosher);
//
//        Attr recipeServeCount = doc.createAttribute("Serve_Count");
//        recipeServeCount.setValue(Integer.toString(recipe.getServeCount()));
//        GeneralE.setAttributeNode(recipeServeCount);


        rootElement.appendChild(GeneralE);




        //Adding Ingredients
        Element ingredientsListE = doc.createElement("Ingredients_List");
        rootElement.appendChild(ingredientsListE);

        for (int i = 0; i<recipe.getIngredients().size(); i++){
            Element ingredientE = doc.createElement("Ingredient");
            Attr ingredientName = doc.createAttribute("Name");
            ingredientName.setValue(recipe.getIngredients().get(i).getName());
            Attr ingredientAmount = doc.createAttribute("Amount");
            ingredientAmount.setValue(recipe.getIngredients().get(i).getAmount());
            Attr ingredientUnits = doc.createAttribute("Units");
            ingredientUnits.setValue(recipe.getIngredients().get(i).getUnits());
            ingredientE.setAttributeNode(ingredientName);
            ingredientE.setAttributeNode(ingredientAmount);
            ingredientE.setAttributeNode(ingredientUnits);
            ingredientsListE.appendChild(ingredientE);
        }


        //Adding Steps
        Element stepsListE = doc.createElement("Steps_List");
        rootElement.appendChild(stepsListE);
        for (int i =0; i<recipe.getSteps().size();i++){
            Element stepE = doc.createElement("Step");
            //StepName
            Attr stepName = doc.createAttribute("Name");
            stepName.setValue(recipe.getSteps().get(i).getName());
            //StepDescription
            Attr stepDescription = doc.createAttribute("Description");
            stepDescription.setValue(recipe.getSteps().get(i).getDescription());
            //StepTime
            Attr stepTime = doc.createAttribute("Time");
            stepTime.setValue(recipe.getSteps().get(i).getTime());
            //StepAction
            Attr stepAction = doc.createAttribute("Action");
            stepAction.setValue(recipe.getSteps().get(i).getAction());
            //stepNumber
            Attr stepNumber = doc.createAttribute("Number");
            stepNumber.setValue(String.valueOf(recipe.getSteps().get(i).getNumber()));
            //Setting Attr and appending
            stepE.setAttributeNode(stepName);
            stepE.setAttributeNode(stepDescription);
            stepE.setAttributeNode(stepTime);
            stepE.setAttributeNode(stepAction);
            stepE.setAttributeNode(stepNumber);
            stepsListE.appendChild(stepE);

        }





        // write dom document to a file
        try (FileOutputStream output =
                     new FileOutputStream(context.getFilesDir().getPath() + "/"  + MyConstants.DOWNLOADED_RECIPE_NAME)) {
            writeXml(doc, output);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }


    //This is the one that actually makes the file available in the app's data that you can see in Android Studio!!
    //As long as you use DownloadFiles, the file is in the phone's data anyway.
    public static void writeXml(Document doc,
                         OutputStream output)
            throws TransformerException {


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);


    }

    public static void uploadXML(Context context, String fileName){

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        StorageReference fStorage = FirebaseStorage.getInstance().getReference("Community Recipes").child(currentUser.getUid()).child(fileName);
        String filesDir = context.getFilesDir().getPath();
        String XMLFilePath = filesDir + "/" + MyConstants.DOWNLOADED_RECIPE_NAME;
        File XMLFile = new File(XMLFilePath);
        Uri XMLUri = Uri.fromFile(XMLFile);
        if (XMLUri!=null){

            StorageReference fRef = fStorage.child(MyConstants.RECIPE_STORAGE_NAME);

            fRef.putFile(XMLUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "Recipe Uploaded!", Toast.LENGTH_LONG).show();

                }
            });
        }
        else
        {
            Toast.makeText(context, "Error, File was not Uploaded", Toast.LENGTH_LONG).show();
        }
    }



    public static  void downloadXML(Context context, String fileName, String path){

        if (isFileExists(context, MyConstants.DOWNLOADED_RECIPE_NAME))
        {
            deleteFile(context, MyConstants.DOWNLOADED_RECIPE_NAME);
        }
        File downloadedRecipe = new File(context.getFilesDir(), MyConstants.DOWNLOADED_RECIPE_NAME);

        //For NONE CUSTOM LESSONS, fileName MUST BE WITH THE EXTENSION
        StorageReference fStorage = FirebaseStorage.getInstance().getReference(path);
        StorageReference fileRef = fStorage.child(fileName);

        fileRef.getFile(downloadedRecipe)
                .addOnSuccessListener(taskSnapshot -> {
                })
                .addOnFailureListener(exception -> {
                    // File download failed
                });
//        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                String strUrl = uri.toString();
////                downloadFiles(context, MyConstants.CURRENTLY_LEARNED_RECIPE,"MyDir" , strUrl);
//
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(context, "Recipe Not Found!", Toast.LENGTH_LONG).show();
//
//            }
//        });
    }

    public static void downloadFiles(Context context, String fileName, String destinationDirectory, String url){
        if (isFileExists(context, fileName))
        {
            deleteFile(context, fileName);
        }

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);

        downloadManager.enqueue(request);
    }
    public static boolean isFileExists(Context context, String filename){


//        File folder1 = new File(context.getExternalFilesDir("MyDir"), filename);
        File folder1 = new File(context.getFilesDir(), filename);
        return folder1.exists();

    }

    public static boolean deleteFile(Context context, String filename) {

//        File folder1 = new File(context.getExternalFilesDir("MyDir"), filename);
        File folder1 = new File(context.getFilesDir(), filename);
        return folder1.delete();
    }



    //PHOTOS

    public static void uploadProfilePhotoToFirebase(Context context, Uri imageUri){
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        FirebaseStorage fStorage = FirebaseStorage.getInstance();
        StorageReference fDownRef = fStorage.getReference("Users").child(currentUser.getUid()).child(MyConstants.PROFILE_PICTURE);
        fDownRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Photo Uploaded!", Toast.LENGTH_LONG).show();

            }
        });
    }


    public static void getProfilePhotoFromFirebase(ImageView iv){
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        FirebaseStorage fStorage = FirebaseStorage.getInstance();
        StorageReference fDownRef = fStorage.getReference("Users").child(currentUser.getUid()).child(MyConstants.PROFILE_PICTURE);
        long MAXBYTES = 1024*1024;
        fDownRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                bitmap = getCircularBitmap(bitmap);
                iv.setImageBitmap(bitmap);
            }
        });
        fDownRef.getBytes(MAXBYTES).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                iv.setImageResource(R.drawable.default_profile_picture);
                Bitmap bm = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                iv.setImageBitmap(myServices.getCircularBitmap(bm));
            }
        });
    }

    //ROUND IMAGE
    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }



    //NAVIGATING THE HOME MENU

    public static void goToHomePage(Context context){
        Intent toHomeScreen = new Intent(context, HomeScreen.class);
        context.startActivity(toHomeScreen);
    }

    public static void goToProfilePage(Context context){
        Intent toProfileScreen = new Intent(context, ProfileScreen.class);
        context.startActivity(toProfileScreen);
    }

    public static void goToCommunityPage(Context context){
        Intent toCommunityScreen = new Intent(context, CommunityScreen.class);
        context.startActivity(toCommunityScreen);
    }



//    public void goToCommunityScreen(Context context){
//        Intent toCommunityScreen = new Intent(context, CommunityScreen.class);
//        context.startActivity(toCommunityScreen);
//    }

}
