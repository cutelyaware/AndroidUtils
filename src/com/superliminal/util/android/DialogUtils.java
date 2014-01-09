package com.superliminal.util.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.*;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class DialogUtils {
    // See bug report: http://code.google.com/p/android/issues/detail?id=2219
    // From workaround at http://stackoverflow.com/questions/1997328/android-clickable-hyperlinks-in-alertdialog
    public static void showHTMLDialog(Context context, String html) {
        // Linkify the message
        final SpannableString s = new SpannableString(Html.fromHtml(html));
        Linkify.addLinks(s, Linkify.ALL);
        final AlertDialog d = new AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok, null)
                        //.setIcon(R.drawable.icon)
                .setMessage(s)
                .create();
        d.show();
        // Make the textview clickable. Must be called after show()
        ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }


    public static interface ChooserListener {
        public void result(int choice, int how);
    }

    /**
     * @return a choice dialog with a radio button for each of the given string "which" choices,
     *         and two "how" buttons with the given names. When one of the how buttons is selected
     *         the given ChooserListener is called with the index of the currently selected radio
     *         button and the index of the how button clicked.
     */
    public static AlertDialog createWhichAndHowChooser(Context context, String title, String[] choices, String how0, String how1, final ChooserListener cl) {
        final int CHOICE_DIPS = 30;
        final RadioGroup group = new RadioGroup(context);
        group.setGravity(Gravity.LEFT);
        group.setPadding(100, 30, 30, 30);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        group.setLayoutParams(fl);
        for(int i = 0; i < choices.length; i++) {
            RadioButton rb = new RadioButton(context);
            rb.setTextSize(TypedValue.COMPLEX_UNIT_DIP, CHOICE_DIPS);
            rb.setText(choices[i]);
            rb.setLayoutParams(fl);
            rb.setId(i);
            group.addView(rb, i, fl);
            if(i == 0)
                rb.setChecked(true);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setView(group)
                .setNegativeButton(how0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        cl.result(group.getCheckedRadioButtonId(), 0);
                    }
                }).setPositiveButton(how1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        cl.result(group.getCheckedRadioButtonId(), 1);
                    }
                });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    /**
     * Same as radio button version above but with a row of buttons with icons or other given drawables.
     */
    public static AlertDialog createWhichAndHowChooser(Context context, String title, String[] choice_names, int[] drawable_ids, String how0, String how1, final ChooserListener cl) {
        final LinearLayout group = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        group.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(10,10,10,10);
        Button first_button = null;
        final int[] selected = new int[1];
        final int selected_color = android.graphics.Color.parseColor("#FFA500");
        for(int i=0; i<choice_names.length; i++) {
            Button b = new Button(context);
            if(i==0)
                first_button = b;
            b.setLayoutParams(params);
            b.setPadding(20,20,20,20);
            b.setText(choice_names[i]);
            if(drawable_ids != null)
                b.setCompoundDrawablesWithIntrinsicBounds(0,0,0,drawable_ids[i]);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int j=0; j<group.getChildCount(); j++) {
                        selected[0] = j;
                        View child = group.getChildAt(j);
                        child.setBackgroundColor(child == v ? selected_color : android.graphics.Color.TRANSPARENT);
                    }
                }
            });
            group.addView(b);
            if(first_button != null)
                first_button.performClick();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setView(group)
                .setNegativeButton(how0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        cl.result(selected[0], 0);
                    }
                })
                .setPositiveButton(how1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        cl.result(selected[0], 1);
                    }
                });
        AlertDialog dialog = builder.create();
        return dialog;
    }

}