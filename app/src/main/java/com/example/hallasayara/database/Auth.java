package com.example.hallasayara.database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hallasayara.activity.DriverDcoumentsActivity;
import com.example.hallasayara.activity.MainActivity;
import com.example.hallasayara.activity.VerificationActivity;
import com.example.hallasayara.core.University;
import com.example.hallasayara.global.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Auth {

    public static final String PHP_GET_UNIVERSITIES = Database.URL_SERVER + "get_universities.php";
    public static final String PHP_CREATE_USER = Database.URL_SERVER + "create_user.php";
    public static final String PHP_VERIFY_USER = Database.URL_SERVER + "verify_user.php";
    public static final String PHP_LOGIN_USER = Database.URL_SERVER + "login_user.php";
    public static final String PHP_SEND_EMAIL = Database.URL_SERVER + "send_email.php";
    public static final String PHP_SEND_SMS = Database.URL_SERVER + "send_sms.php";
    private static final String TAG_UNIVERSITIES = "universities";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_REG_NO = "reg_no";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_DATE_OF_BIRTH = "date_of_birth";
    private static final String TAG_EMAIL_VERIFIED = "email_verified";
    private static final String TAG_PHONE_VERIFIED = "phone_verified";
    private static final String TAG_DRIVER_VERIFIED = "driver_verified";
    private static final String TAG_UNIVERSITY_ID = "uni_id";
    private static final String TAG_CREATED_AT = "created_at";
    private static final String TAG_UPDATED_AT = "updated_at";
    private static final String TAG_DOMAIN = "domain";
    private static final String TAG_VERIFICATION_CODE = "verification_code";

    private Context context;

    public Auth(Context context) {
        this.context = context;
    }

    public void verifyUser(int userId, final boolean emailVerified, final boolean phoneVerified) {

        new AsyncTask<Integer, String, String>() {

            /**
             * Before starting background thread Show Progress Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            /**
             * getting All products from url
             */
            protected String doInBackground(Integer... args) {
                final int id = args[0].intValue();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, PHP_VERIFY_USER, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String message = json.getString(Database.TAG_MESSAGE);
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Log.i("TEST", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TEST", error.getMessage());
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", Integer.toString(id));
                        params.put("email_verified", Boolean.toString(emailVerified));
                        params.put("phone_verified", Boolean.toString(phoneVerified));
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                Volley.newRequestQueue(context).add(stringRequest);
                return null;
            }

            /**
             * After completing background task Dismiss the progress dialog
             **/
            protected void onPostExecute(String string) {
                SharedPreferences sp = context.getSharedPreferences("com.example.hallasayara", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("email_verified", emailVerified);
                editor.putBoolean("phone_verified", phoneVerified);
                editor.commit();
            }

        }.execute(userId);
    }

    public void loginUser(final String email, final String phone) {

        new AsyncTask<String, String, String>() {

            /**
             * Before starting background thread Show Progress Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            /**
             * getting All products from url
             */
            protected String doInBackground(String... args) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, PHP_LOGIN_USER, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getInt(Database.TAG_SUCCESS) == Database.SUCCESS) {
                                Toast.makeText(context, "Welcome back!", Toast.LENGTH_LONG).show();
                                int id = json.getInt(TAG_ID);

                                String name = json.getString(TAG_NAME);
                                String userEmail = json.getString(TAG_EMAIL);
                                String userPhone = json.getString(TAG_PHONE);
                                int universityId = json.getInt(TAG_UNIVERSITY_ID);
                                char gender = ' ';
                                if (!json.isNull(TAG_GENDER))
                                    gender = json.getString(TAG_GENDER).charAt(0);
                                int regNo = 0;
                                if (!json.isNull(TAG_REG_NO))
                                    regNo = json.getInt(TAG_REG_NO);
                                Date dateOfBirth = null;
                                if (!json.isNull(TAG_DATE_OF_BIRTH))
                                    dateOfBirth = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(json.getString(TAG_DATE_OF_BIRTH)).getTime());
                                Timestamp createdAt = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.getString(TAG_CREATED_AT)).getTime());
                                Timestamp updatedAt = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.getString(TAG_UPDATED_AT)).getTime());
                                boolean emailStatus = json.getString(TAG_EMAIL_VERIFIED).equals("1");
                                boolean phoneStatus = json.getString(TAG_PHONE_VERIFIED).equals("1");
                                boolean driverStatus = json.getString(TAG_DRIVER_VERIFIED).equals("1");

                                SharedPreferences sp = context.getSharedPreferences("com.example.hallasayara", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt(TAG_ID, id);
                                editor.putString(TAG_NAME, name);
                                editor.putString(TAG_EMAIL, userEmail);
                                editor.putString(TAG_PHONE, userPhone);
                                editor.putInt(TAG_UNIVERSITY_ID, universityId);
                                editor.putInt(TAG_REG_NO, regNo);
                                editor.putBoolean(TAG_EMAIL_VERIFIED, emailStatus);
                                editor.putBoolean(TAG_PHONE_VERIFIED, phoneStatus);
                                editor.putBoolean(TAG_DRIVER_VERIFIED, driverStatus);
                                String dateString = null;
                                if (dateOfBirth != null)
                                    dateString = dateOfBirth.toString();
                                editor.putString(TAG_DATE_OF_BIRTH, dateString);
                                editor.putString(TAG_CREATED_AT, createdAt.toString());
                                editor.putString(TAG_UPDATED_AT, updatedAt.toString());
                                editor.putString(TAG_GENDER, Character.toString(gender));

                                editor.putString("verify_email", email);
                                editor.putString("verify_phone", phone);
                                editor.commit();

                                Intent intent = new Intent(context, VerificationActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            } else {
                                String message = json.getString(Database.TAG_MESSAGE);
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | ParseException e) {
                            Log.i("TEST", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TEST", error.toString());
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(TAG_EMAIL, email);
                        params.put(TAG_PHONE, phone);
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                Volley.newRequestQueue(context).add(stringRequest);
                return null;
            }

            /**
             * After completing background task Dismiss the progress dialog
             **/
            protected void onPostExecute(String string) {
            }

        }.execute();
    }

    public void createUser(final String name, final String email, final String phone, final University university, final int regNo) {

        new AsyncTask<String, String, String>() {

            /**
             * Before starting background thread Show Progress Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            /**
             * getting All products from url
             */
            protected String doInBackground(String... args) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, PHP_CREATE_USER, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getInt(Database.TAG_SUCCESS) == Database.SUCCESS) {
                                Toast.makeText(context, "Account successfully created!", Toast.LENGTH_LONG).show();
                                int id = json.getInt(TAG_ID);

                                SharedPreferences sp = context.getSharedPreferences("com.example.hallasayara", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt(TAG_ID, id);
                                editor.putString(TAG_NAME, name);
                                editor.putString(TAG_EMAIL, email);
                                editor.putString(TAG_PHONE, phone);
                                editor.putInt(TAG_UNIVERSITY_ID, university.getId());
                                editor.putInt(TAG_REG_NO, regNo);

                                editor.putString("verify_email", email);
                                editor.putString("verify_phone", phone);
                                editor.commit();

                                Intent intent = new Intent(context, VerificationActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            } else {
                                String message = json.getString(Database.TAG_MESSAGE);
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.i("TEST", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TEST", error.getMessage());
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(TAG_NAME, name);
                        params.put(TAG_EMAIL, email);
                        params.put(TAG_PHONE, phone);
                        params.put(TAG_REG_NO, Integer.toString(regNo));
                        params.put(TAG_UNIVERSITY_ID, Integer.toString(university.getId()));
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        params.put(TAG_CREATED_AT, timestamp.toString());
                        params.put(TAG_UPDATED_AT, timestamp.toString());
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                Volley.newRequestQueue(context).add(stringRequest);
                return null;
            }

            /**
             * After completing background task Dismiss the progress dialog
             **/
            protected void onPostExecute(String string) {
            }

        }.execute();
    }


    public void loadUniversityList() {
        new AsyncTask<String, String, String>() {

            /**
             * Before starting background thread Show Progress Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            /**
             * getting All products from url
             */
            protected String doInBackground(String... args) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, PHP_GET_UNIVERSITIES, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getInt(Database.TAG_SUCCESS) == Database.SUCCESS) {
                                        JSONArray jUniversities = response.getJSONArray(TAG_UNIVERSITIES);
                                        for (int i = 0; i < jUniversities.length(); i++) {
                                            JSONObject jUniversity = jUniversities.getJSONObject(i);
                                            int id = jUniversity.getInt(TAG_ID);
                                            String name = jUniversity.getString(TAG_NAME);
                                            String domain = null;
                                            if (!jUniversity.isNull(TAG_DOMAIN))
                                                domain = jUniversity.getString(TAG_DOMAIN);
                                            Database.getUniversityList().add(new University(id, name, domain));
                                        }
                                    } else
                                        Toast.makeText(context, response.getString(Database.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    Log.i("TEST", e.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.getMessage() != null && !error.getMessage().isEmpty())
                                    Log.i("TEST", error.getMessage());
                            }
                        });
                Volley.newRequestQueue(context).add(jsonObjectRequest);
                return null;
            }

            /**
             * After completing background task Dismiss the progress dialog
             **/
            protected void onPostExecute(String string) {
                // dismiss the dialog after getting all products
            }

        }.execute();
    }

    public void sendEmailVerification(final String email, final int code, final String name) {
        new AsyncTask<String, String, String>() {

            /**
             * Before starting background thread Show Progress Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            protected String doInBackground(String... args) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, PHP_SEND_EMAIL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getInt(Database.TAG_SUCCESS) == Database.SUCCESS)
                                Toast.makeText(context, "Email verification sent!", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(context, "Email verification failed!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Log.i("sendEmailVerification1", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("sendEmailVerification2", error.toString());
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(TAG_EMAIL, email);
                        params.put(TAG_VERIFICATION_CODE, Integer.toString(code));
                        params.put(TAG_NAME, name);
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(context).add(stringRequest);
                return null;
            }

            /**
             * After completing background task Dismiss the progress dialog
             **/
            protected void onPostExecute(String string) {
            }

        }.execute();
    }

    public void sendPhoneVerification(final String phone, final int code, final String name) {
        new AsyncTask<String, String, String>() {

            /**
             * Before starting background thread Show Progress Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            protected String doInBackground(String... args) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, PHP_SEND_SMS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getInt(Database.TAG_SUCCESS) == Database.SUCCESS)
                                Toast.makeText(context, "Phone verification sent!", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(context, "Email verification failed!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Log.i("sendPhoneVerification1", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("sendPhoneVerification2", error.toString());
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(TAG_PHONE, phone);
                        params.put(TAG_VERIFICATION_CODE, Integer.toString(code));
                        params.put(TAG_NAME, name);
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                Volley.newRequestQueue(context).add(stringRequest);
                return null;
            }

            /**
             * After completing background task Dismiss the progress dialog
             **/
            protected void onPostExecute(String string) {
            }

        }.execute();
    }

}
