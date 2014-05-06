package mx.gdgipn.parsetest.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import mx.gdgipn.parsetest.app.ParseTestApplication;
import mx.gdgipn.parsetest.app.R;

public class UserDetailsActivity extends ActionBarActivity {

    private ProfilePictureView userProfilePictureView;
    private TextView userNameView;
    private TextView userLocationView;
    private TextView userGenderView;
    private TextView userDateOfBirthView;
    private TextView userRelationshipView;
    private TextView userEmailView;
    private Button logoutButton;
    private ActionBar actionbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#44619d"));
        actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(colorDrawable);

        userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
        userNameView = (TextView) findViewById(R.id.userName);
        userLocationView = (TextView) findViewById(R.id.userLocation);
        userGenderView = (TextView) findViewById(R.id.userGender);
        userDateOfBirthView = (TextView) findViewById(R.id.userDateOfBirth);
        userRelationshipView = (TextView) findViewById(R.id.userRelationship);
        userEmailView = (TextView) findViewById(R.id.userEmail);

        logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutButtonClicked();
            }
        });

        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            makeMeRequest();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            updateViewsWithProfileInfo();
        } else {

            Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void makeMeRequest() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {

                            JSONObject userProfile = new JSONObject();

                            try {
                                userProfile.put("facebookId", user.getId());
                                userProfile.put("name", user.getName());
                                userProfile.put("firstName", user.getFirstName());

                                if (user.getLocation().getProperty("name") != null) {
                                    userProfile.put("location",user.getLocation().getProperty("name"));
                                }

                                if (user.getProperty("gender") != null) {
                                    userProfile.put("gender",user.getProperty("gender"));
                                }

                                if (user.getProperty("email") != null) {
                                    userProfile.put("email",user.getProperty("email"));
                                }

                                if (user.getBirthday() != null) {
                                    userProfile.put("birthday",user.getBirthday());
                                }

                                if (user.getProperty("relationship_status") != null) {
                                    userProfile.put("relationship_status",user.getProperty("relationship_status"));
                                }

                                ParseUser currentUser = ParseUser.getCurrentUser();

                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                                updateViewsWithProfileInfo();

                            } catch (JSONException e) {
                                Log.d(ParseTestApplication.TAG, "Error parsing returned user data.");
                            }

                        } else if (response.getError() != null) {

                            if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
                                    || (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {

                                Log.d(ParseTestApplication.TAG,  "The facebook session was invalidated.");
                                onLogoutButtonClicked();
                            } else {
                                Log.d(ParseTestApplication.TAG, "Some other error: " + response.getError() .getErrorMessage());
                            }
                        }
                    }
                });

        request.executeAsync();
    }

    private void updateViewsWithProfileInfo() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.get("profile") != null) {

            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {

                if (userProfile.getString("facebookId") != null) {
                    String facebookId = userProfile.get("facebookId") .toString();
                    userProfilePictureView.setProfileId(facebookId);
                } else {
                    userProfilePictureView.setProfileId(null);
                }

                if (userProfile.getString("name") != null) {
                    userNameView.setText(userProfile.getString("name"));
                    actionbar.setTitle("Hola "+userProfile.getString("firstName")+"!");
                } else {
                    userNameView.setText("");
                }

                if (userProfile.getString("location") != null) {
                    userLocationView.setText(userProfile.getString("location"));
                } else {
                    userLocationView.setText("");
                }

                if (userProfile.getString("gender") != null) {
                    userGenderView.setText(userProfile.getString("gender"));
                } else {
                    userGenderView.setText("");
                }

                if (userProfile.getString("email") != null) {
                    userEmailView.setText(userProfile.getString("email"));
                } else {
                    userEmailView.setText("");
                }

                if (userProfile.getString("birthday") != null) {
                    userDateOfBirthView.setText(userProfile
                            .getString("birthday"));
                } else {
                    userDateOfBirthView.setText("");
                }

                if (userProfile.getString("relationship_status") != null) {
                    userRelationshipView.setText(userProfile
                            .getString("relationship_status"));
                } else {
                    userRelationshipView.setText("");
                }

            } catch (JSONException e) {
                Log.d(ParseTestApplication.TAG,"Error parsing saved user data.");
            }

        }
    }

    private void onLogoutButtonClicked() {
        ParseUser.logOut();
        Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



}
