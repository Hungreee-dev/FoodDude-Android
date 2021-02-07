package com.example.dubstep.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dubstep.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactUsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactUsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactUsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ContactUsFragment newInstance(String param1, String param2) {
        ContactUsFragment fragment = new ContactUsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.callUsCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Uri uri = Uri.parse("tel:6371830551");
                    Intent intent = new Intent(Intent.ACTION_DIAL,uri);
                    startActivity(intent);
            }
        });

        view.findViewById(R.id.mailUsCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse("mailto:info@fooddude.in"));
//                mailIntent.putExtra(Intent.EXTRA_EMAIL,"info@fooddude.in");
                try {
                    if (mailIntent.resolveActivity(getActivity().getPackageManager())!=null){
                        startActivity(mailIntent);
                    }
                } catch (NullPointerException e){
                    Toast.makeText(getActivity(), "Can't get any app for sending email", Toast.LENGTH_SHORT).show();
                }catch (ActivityNotFoundException e){
                    Toast.makeText(getActivity(), "No applications found to search a website", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.websiteCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri webUri = Uri.parse("https://fooddude.in");
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,webUri);
                    PackageManager packageManager = getActivity().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent,
                            PackageManager.MATCH_DEFAULT_ONLY);

                    if (activities.size()>0){
                        startActivity(webIntent);
                    } else {
                        Toast.makeText(getActivity(), "No applications found to search a website", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException | ActivityNotFoundException e){
                    Toast.makeText(getActivity(), "No applications found to search a website", Toast.LENGTH_SHORT).show();
                }

            }
        });

        view.findViewById(R.id.facebookImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri facebookUri = Uri.parse("https://m.facebook.com/");
                Intent facebookIntent = new Intent("android.intent.category.LAUNCHER",facebookUri);
                facebookIntent.setClassName("com.facebook.katana", "com.facebook.katana.LoginActivity");

                try {
                    startActivity(facebookIntent);
                } catch (ActivityNotFoundException e){
                    Toast.makeText(getActivity(), "Facebook App not found", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://m.facebook.com")));
                }
            }
        });

        view.findViewById(R.id.instagramImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri instagramUri = Uri.parse("https://www.instagram.com/");
                Intent instagramIntent = new Intent("android.intent.category.LAUNCHER",instagramUri);
                instagramIntent.setClassName("com.instagram.android","com.instagram.android.activity.MainTabActivity");

                try {
                    startActivity(instagramIntent);
                } catch (ActivityNotFoundException e){
                    Toast.makeText(getActivity(), "Instagram App not found", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.instagram.com")));
                }

            }
        });

        view.findViewById(R.id.addressCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri addressUri = Uri.parse("https://maps.app.goo.gl/YDNMyb8gEbh2Wm5X8");
                startActivity(new Intent(Intent.ACTION_VIEW,addressUri));
            }
        });
    }
}