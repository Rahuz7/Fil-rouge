package activite;
import activite.Activity.*;
import java.util.*;
import scheduleio.*;

public class Main{
    
    //To silence the unchecked conversion warnings
    @SuppressWarnings("unchecked") 
    public static void main(String args[]){
        Helper h = new Helper();
        Reader inputs = new Reader(args[0], args[1]);

        List<Activity> activities = inputs.getActivities();
        ArrayList<Constraint> constraints = inputs.readConstraints();
        Verifier perfect_edt = new Verifier();

        RandomScheduler schedule = new RandomScheduler();
        for(Activity activity : activities){
            schedule.add(activity);
        }
        for(Constraint constraint : constraints){
            schedule.add(constraint);
            perfect_edt.add(constraint);
        }

        HashMap<Activity, GregorianCalendar> edt = new HashMap<Activity, GregorianCalendar>();

        //Adding options
        if(args[2].equals("p")){
            //We added this so that we could use the Verifier class at least once in the project
            while(true){
                edt = schedule.satisfiedTimetable(1);
                if(perfect_edt.verify(edt) == true){
                    break;
                }
            }
        }
        else if (args[2].equals("np")){
            edt = schedule.satisfiedTimetable(40);
        }

        if(args[3].equals("c")){
            h.printSortedTimetable(edt);
        }
        else if (args[3].equals("nc")){
            h.printTimetable(edt);
        }
    }
}