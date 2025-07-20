package com.example.note_taking_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int ANIMATION_DURATION = 2800; // Total animation time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Start animations immediately
        startAnimations();
    }

    private void startAnimations() {
        ImageView logo = findViewById(R.id.splashLogo);
        TextView title = findViewById(R.id.appTitle);
        TextView subtitle = findViewById(R.id.appSubtitle);
        ProgressBar progress = findViewById(R.id.loadingProgress);

        // Logo fade in and scale animation
        logo.setAlpha(0f);
        logo.setScaleX(0.5f);
        logo.setScaleY(0.5f);
        logo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new android.view.animation.OvershootInterpolator())
                .start();

        // Title slide up animation
        title.setAlpha(0f);
        title.setTranslationY(50f);
        title.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(400)
                .start();

        // Subtitle fade in
        subtitle.setAlpha(0f);
        subtitle.animate()
                .alpha(0.9f)
                .setDuration(600)
                .setStartDelay(600)
                .start();

        // Progress bar animation
        progress.setAlpha(0f);
        progress.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(800)
                .start();

        // Simulate loading progress
        animateProgress(progress);
    }

    private void animateProgress(ProgressBar progressBar) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        progressAnimator.setDuration(2000);
        progressAnimator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        progressAnimator.setStartDelay(800); // Start after progress bar appears
        progressAnimator.start();

        // Navigate to main activity after animation completes
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Navigate to main activity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                // Add smooth transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}