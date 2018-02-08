package com.lw.thewar.common;

import com.lw.thewar.vo.GroupUsers;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class GroupUsersComparator implements Comparator<GroupUsers> {

    private Collator instance;

    private Collator getCollator(){
        synchronized(Collator.class){
            if(null == instance){
                instance = Collator.getInstance(Locale.CHINA);
            }
        }
        return instance;
    }
    @Override
    public int compare(GroupUsers o1, GroupUsers o2) {

        int a = o2.getAccess()- o1.getAccess();
        if(a != 0){
            return (a < 0) ? 2 : -1;
        }else {
            a = getCollator().compare(o2.getName(),o1.getName());
            return (a < 0) ? 1 : -2;
        }
    }
//    @Override
//    public int compare(GroupUsers o1, GroupUsers o2) {
//        int a = o2.getAccess()- o1.getAccess();
//            if(a != 0) {
//                return (a < 0) ? 1 : -1;
//            }else{
//                return 0;
//            }
//    }
}
