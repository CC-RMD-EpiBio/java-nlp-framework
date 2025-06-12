/*******************************************************************************
 * ---------------------------------------------------------------------------
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                        Epidemiology and Biostatistics Branch 
 *                                         2019
 *    
 *  This work is licensed under the Creative Commons Attribution 4.0 International License. 
 *  
 *  This license allows you to use, share and  adapt for any purpose, provided:
 *     Provide attribution to the creators of this work within your work.
 *     Indicate if changes were made to this work.
 *     No claim to merchantability, implied warranty, or liability can be made.
 *     
 *   When attributing this code, please make reference to
 *    [citation/url here] .  
 *    
 *     In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 *  
 *  To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.Comparator;

public class IntervalData implements Comparable<IntervalData>{
 
	private int x;
	private int y;
	private float quantity;
 
	public IntervalData(int x, int  y, float quantity) {
		super();
		this.x = x;
		this.y = y;
		this.quantity = quantity;
	}
 
	public IntervalData(IntervalData I) {
		super();
		this.x = I.getX();
		this.y = I.getY();
		this.quantity = I.getQuantity();
	}
	public int getX() {
		return x;
	}
	public void setX(int X) {
		this.x = X;
	}
	public int getY() {
		return y;
	}
	public void setY(int Y) {
		this.y = Y;
	}
	public float getQuantity() {
		return quantity;
	}
	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}
 /*
	public float compareTo(IntervalData compareFruit) {
 
		float compareQuantity = compareFruit.getQuantity(); 
 
		//ascending order
		return this.quantity - compareQuantity;
 
		//descending order
		//return compareQuantity - this.quantity;
 
	}
 */
	public static Comparator<IntervalData> IntervalDataComparator 
                          = new Comparator<IntervalData>() {
 
	    public int compare(IntervalData fruit1, IntervalData fruit2) {
 
	      int fruitName1 = fruit1.getX();
	      int fruitName2 = fruit2.getX();
 
	      //ascending order
	      return fruitName1-fruitName2;
 
	      //descending order
	      //
	    }
 
	};

public int compareTo(IntervalData o) {
	// TODO Auto-generated method stub
	return this.x - o.getX();
	//return 0;
}
}
