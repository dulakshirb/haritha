package com.haritha.haritha_farmer;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfilePictureUploadFragment extends Fragment {

    private View view;
    private ImageView img_profile_dp;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_picture_upload, container, false);

        Button btn_choose_picture = view.findViewById(R.id.btn_choose_picture);
        Button btn_upload_picture = view.findViewById(R.id.btn_upload_picture);
        img_profile_dp = view.findViewById(R.id.img_profile_dp);
        loadingBar = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("FarmerProfilePictures");

        //set user's current DP
        if (currentUser.getPhotoUrl() != null) {
            Uri uri = currentUser.getPhotoUrl();
            Picasso.get().load(uri).into(img_profile_dp);
        }

        //choose image
        btn_choose_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        //upload image into firebase
        btn_upload_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingBar.setTitle("Profile picture");
                loadingBar.setMessage("Uploading...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();
                uploadPic();
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            img_profile_dp.setImageURI(uriImage);
        }
    }

    private void uploadPic() {
        if (uriImage != null) {
            StorageReference fileReference = storageReference.child(mAuth.getCurrentUser().getUid() + "." + getFileExtension(uriImage));

            //upload image to storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            currentUser = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            currentUser.updateProfile(profileChangeRequest);

                        }
                    });

                    loadingBar.dismiss();
                    Toast.makeText(getActivity(), "Image Upload Successful!", Toast.LENGTH_SHORT).show();

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment ProfileFragment = new ProfileFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, ProfileFragment).commit();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingBar.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            loadingBar.dismiss();
            Toast.makeText(getActivity(), "No File Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    //CREATE Action bar profile menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.menu_update_profile) {
            fragment = new ProfileUpdateFragment();
            loadFragment(fragment);
        } else if (id == R.id.menu_update_email) {
            fragment = new ProfileUpdateEmailFragment();
            loadFragment(fragment);
        } else if (id == R.id.menu_change_password) {
            fragment = new ProfileChangePasswordFragment();
            loadFragment(fragment);
        } else if (id == R.id.menu_delete_profile) {
            fragment = new ProfileDeleteFragment();
            loadFragment(fragment);
        } else if (id == R.id.menu_logout) {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
            sendUserToMainActivity();
        } else {
            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        getActivity().finish();
    }
}