package com.nuevoshorizontes.nhstream;

import android.os.Bundle;

/**
 * Created by fonseca on 10/16/17.
 */

public class LiveActivityChild extends LiveActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        child = this;
    }

    //---move following onResume() in parent as following eg:
    /*
    *@Override
    *       protected void onResume() {
    *           super.onResume();
    *           if(null != child){
    *           AppContext.registerMemoryListener(this);
    *           }
    *       }
    */
    @Override
    protected void onResume() {
        super.onResume();
        NetplayAplication.registerMemoryListener(this);
    }

    @Override
    public void goodTimeToReleaseMemory() {
        super.goodTimeToReleaseMemory();
//remove your Cache etc here
    }

    //--NO Need because parent implementation will be called first, just for the sake of clarity
    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (null != child)
                NetplayAplication.unregisterMemoryListener(child);
        } catch (Exception e) {

        }
    }

}