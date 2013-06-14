/*
 * Copyright (c) 2010, Jeffrey F. Cole
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 	Redistributions of source code must retain the above copyright notice, this
 * 	list of conditions and the following disclaimer.
 * 
 * 	Redistributions in binary form must reproduce the above copyright notice, 
 * 	this list of conditions and the following disclaimer in the documentation 
 * 	and/or other materials provided with the distribution.
 * 
 * 	Neither the name of the technologichron.net nor the names of its contributors 
 * 	may be used to endorse or promote products derived from this software 
 * 	without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package quelltextfabrik.gymlog.control;

import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * A simple layout group that provides a numeric text area with two buttons to
 * increment or decrement the value in the text area. Holding either button
 * will auto increment the value up or down appropriately. 
 * 
 * @author Jeffrey F. Cole
 *
 */
public class WeightPicker extends LinearLayout {

	private final long REPEAT_DELAY = 50;
	
	private final int ELEMENT_HEIGHT = 50;
	private final int ELEMENT_WIDTH = ELEMENT_HEIGHT*2; // you're all squares, yo
	
	private final int MINIMUM = 0;
	private final int MAXIMUM = 999;
	
    protected Double mCurrent;
    protected Double mPrevious;
	
	Button decrement;
	Button bigDecrement;
	LinearLayout decView;
	
	Button increment;
	Button bigIncrement;
	LinearLayout incView;
	
	public EditText valueText;
	
	private Handler repeatUpdateHandler = new Handler();
	
	private boolean autoIncrement = false;
	private boolean autoDecrement = false;

	/**
	 * This little guy handles the auto part of the auto incrementing feature.
	 * In doing so it instantiates itself. There has to be a pattern name for
	 * that...
	 * 
	 * @author Jeffrey F. Cole
	 *
	 */
	class RepetetiveUpdater implements Runnable {
		public void run() {
			if( autoIncrement ){
				increment();
				repeatUpdateHandler.postDelayed( new RepetetiveUpdater(), REPEAT_DELAY );
			} else if( autoDecrement ){
				decrement();
				repeatUpdateHandler.postDelayed( new RepetetiveUpdater(), REPEAT_DELAY );
			}
		}
	}
	
	public WeightPicker( Context context, AttributeSet attributeSet ) {
		super(context, attributeSet);
		
		this.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) );
		
		LayoutParams elementParams = new LinearLayout.LayoutParams( ELEMENT_WIDTH, ELEMENT_HEIGHT);
		LayoutParams buttonParams = new LinearLayout.LayoutParams( ELEMENT_WIDTH/2, ELEMENT_HEIGHT);
		
		// --- Init the individual elements ---------------------------------------------------------------------------
		// --- Init increments --------------------------------------------------------------------
		incView = new LinearLayout(context);
		incView.setOrientation(HORIZONTAL);		
		initIncrementButton( context );
		initBigIncrementButton( context );
		
		// --- Init decrements --------------------------------------------------------------------
		decView = new LinearLayout(context);
		decView.setOrientation(HORIZONTAL);
		initDecrementButton( context );
		initBigDecrementButton( context );
		
		// --- Init edittext field ----------------------------------------------------------------
		initValueEditText( context );
				
		// --- Finally, add views -----------------------------------------------------------------
		incView.addView( increment, buttonParams );	
		incView.addView( bigIncrement, buttonParams );
		addView(incView, elementParams);
		
		addView( valueText, elementParams );
		
		decView.addView( decrement, buttonParams );
		decView.addView( bigDecrement, buttonParams );
		addView(decView, elementParams);
	}
	
	private void initIncrementButton( Context context){
		increment = new Button( context );
		increment.setTextSize( 25 );
		increment.setText( "+" );
		
		// Increment once for a click
		increment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	increment();
            }
        });
		
		// Auto increment for a long click
		increment.setOnLongClickListener( 
				new View.OnLongClickListener(){
					public boolean onLongClick(View arg0) {
						autoIncrement = true;
						repeatUpdateHandler.post( new RepetetiveUpdater() );
						return false;
					}
				}
		);
		
		// When the button is released, if we're auto incrementing, stop
		increment.setOnTouchListener( new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_UP && autoIncrement ){
					autoIncrement = false;
				}
				return false;
			}
		});
	}
	
	private void initBigIncrementButton( Context context){
		bigIncrement = new Button( context );
		bigIncrement.setTextSize( 25 );
		bigIncrement.setText( "++" );
		
		// Increment once for a click
		bigIncrement.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	bigIncrement();
            }
        });
		
		// Auto increment for a long click
		bigIncrement.setOnLongClickListener( 
				new View.OnLongClickListener(){
					public boolean onLongClick(View arg0) {
						autoIncrement = true;
						repeatUpdateHandler.post( new RepetetiveUpdater() );
						return false;
					}
				}
		);
		
		// When the button is released, if we're auto incrementing, stop
		bigIncrement.setOnTouchListener( new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_UP && autoIncrement ){
					autoIncrement = false;
				}
				return false;
			}
		});
	}
	
	private void initValueEditText( Context context){
		
		mCurrent = new Double( 0 );
		
		valueText = new EditText( context );
		valueText.setTextSize(25);
		
		// Since we're a number that gets affected by the button, we need to be
		// ready to change the numeric value with a simple ++/--, so whenever
		// the value is changed with a keyboard, convert that text value to a
		// number. We can set the text area to only allow numeric input, but 
		// even so, a carriage return can get hacked through. To prevent this
		// little quirk from causing a crash, store the value of the internal
		// number before attempting to parse the changed value in the text area
		// so we can revert to that in case the text change causes an invalid
		// number
		valueText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int arg1, KeyEvent event) {
				double backupValue = mCurrent;
				try {
					mCurrent = Double.parseDouble( ((EditText)v).getText().toString() );
				} catch( NumberFormatException nfe ){
					mCurrent = backupValue;
				}
				return false;
			}
		});
		
		// Highlight the number when we get focus
		valueText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if( hasFocus ){
					((EditText)v).selectAll();
				}
			}
		});
		valueText.setGravity( Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL );
		valueText.setText( mCurrent.toString() );
		valueText.setInputType( InputType.TYPE_CLASS_NUMBER );
	}
	
	private void initDecrementButton( Context context){
		decrement = new Button( context );
		decrement.setTextSize( 25 );
		decrement.setText( "-" );
		

		// Decrement once for a click
		decrement.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	decrement();
            }
        });
		

		// Auto Decrement for a long click
		decrement.setOnLongClickListener( 
				new View.OnLongClickListener(){
					public boolean onLongClick(View arg0) {
						autoDecrement = true;
						repeatUpdateHandler.post( new RepetetiveUpdater() );
						return false;
					}
				}
		);
		
		// When the button is released, if we're auto decrementing, stop
		decrement.setOnTouchListener( new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_UP && autoDecrement ){
					autoDecrement = false;
				}
				return false;
			}
		});
	}

	private void initBigDecrementButton( Context context){
		bigDecrement = new Button( context );
		bigDecrement.setTextSize( 25 );
		bigDecrement.setText( "--" );
		

		// Decrement once for a click
		bigDecrement.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	bigDecrement();
            }
        });
		

		// Auto Decrement for a long click
		bigDecrement.setOnLongClickListener( 
				new View.OnLongClickListener(){
					public boolean onLongClick(View arg0) {
						autoDecrement = true;
						repeatUpdateHandler.post( new RepetetiveUpdater() );
						return false;
					}
				}
		);
		
		// When the button is released, if we're auto decrementing, stop
		bigDecrement.setOnTouchListener( new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_UP && autoDecrement ){
					autoDecrement = false;
				}
				return false;
			}
		});
	}
	
	public void increment(){
		if( mCurrent < MAXIMUM ){
			mPrevious = mCurrent;
			mCurrent += 0.5;
			valueText.setText( mCurrent.toString() );
			notifyChange();
		}
	}
	
	public void bigIncrement(){
		if( mCurrent < MAXIMUM ){
			mPrevious = mCurrent;
			mCurrent += 2.5;
			valueText.setText( mCurrent.toString() );
			notifyChange();
		}
	}

	public void decrement(){
		if( mCurrent > MINIMUM ){
			mPrevious = mCurrent;
			mCurrent -= 0.5;
			valueText.setText(mCurrent.toString() );
			notifyChange();
		}
	}

	public void bigDecrement(){
		if( mCurrent > MINIMUM ){
			mPrevious = mCurrent;
			mCurrent -= 2.5;
			valueText.setText(mCurrent.toString());
			notifyChange();
		}
	}
	
	public Double getValue(){
		return mCurrent;
	}
	
	public void setValue( Double value ){
		if( value > MAXIMUM ) value = (double) MAXIMUM;
		if( value >= 0 ){
			this.mPrevious = this.mCurrent;
			this.mCurrent = value;
			valueText.setText( this.mCurrent.toString() );
		}
	}
	
	// --- On Change
    private OnChangedListener mListener;

    public void setOnChangeListener(OnChangedListener listener) {
        mListener = listener;
    }    

    public interface OnChangedListener {
        void onChanged(WeightPicker picker, Double oldVal, Double newVal);
    }

    protected void notifyChange() {
        if (mListener != null) {
            mListener.onChanged(this, mPrevious, mCurrent);
        }
    }
}
