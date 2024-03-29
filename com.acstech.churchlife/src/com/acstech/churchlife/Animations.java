package com.acstech.churchlife;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;


//FUTURE:  Move these to xml files and delete this class (use the R.anim structure instead)
//
// Example:		Animation a = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.slide_left_to_right);        			        		         			         		
//      		Animation b = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.slide_right_to_left);
	
public class Animations {
	
	public static Animation inFromRightAnimation() {
		  Animation inFromRight = new TranslateAnimation(
		    Animation.RELATIVE_TO_PARENT, +1.0f,
		    Animation.RELATIVE_TO_PARENT, 0.0f,
		    Animation.RELATIVE_TO_PARENT, 0.0f,
		    Animation.RELATIVE_TO_PARENT, 0.0f);
		  inFromRight.setDuration(500);
		  inFromRight.setInterpolator(new AccelerateInterpolator());
		  return inFromRight;
		 }

	public static Animation outToLeftAnimation() {
		  Animation outtoLeft = new TranslateAnimation(
		    Animation.RELATIVE_TO_PARENT, 0.0f,
		    Animation.RELATIVE_TO_PARENT, -1.0f,
		    Animation.RELATIVE_TO_PARENT, 0.0f,
		    Animation.RELATIVE_TO_PARENT, 0.0f);
		  outtoLeft.setDuration(500);
		  outtoLeft.setInterpolator(new AccelerateInterpolator());
		  return outtoLeft;
		 }

	public static Animation inFromLeftAnimation() {
		  Animation inFromLeft = new TranslateAnimation(
		    Animation.RELATIVE_TO_PARENT, -1.0f,
		    Animation.RELATIVE_TO_PARENT, 0.0f,
		    Animation.RELATIVE_TO_PARENT, 0.0f,
		    Animation.RELATIVE_TO_PARENT, 0.0f);
		  inFromLeft.setDuration(500);
		  inFromLeft.setInterpolator(new AccelerateInterpolator());
		  return inFromLeft;
		 }

	public static Animation outToRightAnimation() {
		  Animation outtoRight = new TranslateAnimation(
		    Animation.RELATIVE_TO_PARENT, 0.0f,
		    Animation.RELATIVE_TO_PARENT, +1.0f,
		    Animation.RELATIVE_TO_PARENT, 0.0f,
		    Animation.RELATIVE_TO_PARENT, 0.0f);
		  outtoRight.setDuration(500);
		  outtoRight.setInterpolator(new AccelerateInterpolator());
		  return outtoRight;
		 }
		 
}
