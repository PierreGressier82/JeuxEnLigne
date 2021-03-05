package com.pigredorou.jeuxenvisio;

import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LeRoiDesNainsActivity extends JeuEnVisioActivity implements View.OnLongClickListener, View.OnTouchListener, View.OnDragListener {

    View vueDepart = null;
    View vueArrivee = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Affiche la vue
        setContentView(R.layout.activity_le_roi_des_nains);

        super.onCreate(savedInstanceState);

        // TEST DRAG & DROP
        findViewById(R.id.table_carte_image_joueur1).setOnTouchListener(this);
        findViewById(R.id.table_carte_image_joueur2).setOnTouchListener(this);
        findViewById(R.id.table_carte_image_joueur3).setOnTouchListener(this);
        findViewById(R.id.table_carte_image_joueur4).setOnTouchListener(this);
        findViewById(R.id.table_carte_image_joueur5).setOnTouchListener(this);
        findViewById(R.id.source).setOnDragListener(this);
        findViewById(R.id.cible).setOnDragListener(this);

        // TODO : A retirer
        findViewById(R.id.chargement).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }


    @Override
    public boolean onDrag(View v, DragEvent event) {
        Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Sauvegarde du contexte de départ
                vueDepart = v;
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackground(enterShape);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackground(normalShape);
                break;
            case DragEvent.ACTION_DROP:
                vueArrivee = v;
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackground(normalShape);
                View view = (View) event.getLocalState();
                if (event.getResult())
                    // Dropped, reassign View to ViewGroup
                    v = vueArrivee;
                else {
                    Toast.makeText(getBaseContext(), "Drop raté", Toast.LENGTH_SHORT).show();
                    v = vueDepart;
                }
                ViewGroup owner = (ViewGroup) view.getParent();
                owner.removeView(view);
                LinearLayout container = (LinearLayout) v;
                container.addView(view);
                view.setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            v.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, "LONG CLIC", Toast.LENGTH_SHORT).show();
        return false;
    }
}
