package activite;
import java.util.*;
import scheduleio.*;

public class Activity{
    protected String name;
    protected int duration; // in minutes

    public Activity(String a, int b){
        name = a;
        duration = b;
    }

    public String toString(){
        return "Activity: " + this.name + "\nDuration: " + this.duration + " minutes\n";
    }
}

// A group of helpder functions we use often.
class Helper{

    //Converting dates from an HH:MM format to a MM format
    public static int convertDate(GregorianCalendar date){
        return (date.get(Calendar.HOUR_OF_DAY) * 60) + date.get(Calendar.MINUTE);
    }

    //Printing activities and their dates from a timetable
    public static void printActivity(Activity a, GregorianCalendar d){
        System.out.println("----\n" + a + "Date = " + d.get(Calendar.HOUR_OF_DAY) + "h " + d.get(Calendar.MINUTE) + "m");
    }

    //Printing entire timetables
    public static void printTimetable(HashMap<Activity, GregorianCalendar> timetable){
        for(Map.Entry<Activity, GregorianCalendar> entry : timetable.entrySet()){
            printActivity(entry.getKey(), entry.getValue());
        }
    }

    //Printing entire timetables in chronological order
    public static void printSortedTimetable(HashMap<Activity, GregorianCalendar> timetable){
        int i = 0;
        List<GregorianCalendar> dates = new ArrayList<>(timetable.values());
        List<Integer> sorted_hours = new ArrayList<>();
        List<Integer> sorted_minutes = new ArrayList<>();
        Collections.sort(dates);

        for(GregorianCalendar date : dates){
            sorted_hours.add(date.get(Calendar.HOUR_OF_DAY));
            sorted_minutes.add(date.get(Calendar.MINUTE));
        }

        while(true){
            for(Map.Entry<Activity, GregorianCalendar> entry : timetable.entrySet()){
                if(i == dates.size()){
                    break;
                }
                if((entry.getValue().get(Calendar.HOUR_OF_DAY) == sorted_hours.get(i)) && (entry.getValue().get(Calendar.MINUTE) == sorted_minutes.get(i)) && (i < dates.size())){
                    printActivity(entry.getKey(), entry.getValue());
                    i += 1;
                }

            }
            if(i == dates.size()){
                break;
            }
        }
    }
}

interface Constraint{
    public boolean isSatisfiedBySchedule(HashMap<Activity, GregorianCalendar> edt);
}

abstract class BinaryConstraint implements Constraint{
    protected Activity first;
    protected Activity second;

    public BinaryConstraint(Activity a, Activity b){
        first = a;
        second = b;
    }
    protected abstract boolean isSatisfied(GregorianCalendar date1, GregorianCalendar date2);

    //Tests constraints depending on the order given by a timetable
    public boolean isSatisfiedBySchedule(HashMap<Activity, GregorianCalendar> edt){
        return isSatisfied(edt.get(this.first), edt.get(this.second));
    }
}

class PrecedenceConstraint extends BinaryConstraint{
    public PrecedenceConstraint(Activity a, Activity b){
        super(a, b);
    }

    protected boolean isSatisfied(GregorianCalendar date1, GregorianCalendar date2){
        if(((Helper.convertDate(date1)) + first.duration) <= (Helper.convertDate(date2))){
            return true;
        }
        return false;
    }

    public String toString(){
        return first.name + " is before " + second.name;
    }
}

class PrecedenceConstraintWithGap extends PrecedenceConstraint{
    protected int gap;

    public PrecedenceConstraintWithGap(Activity a, Activity b, int c){
        super(a, b);
        gap = c;
    }

    protected boolean isSatisfied(GregorianCalendar date1, GregorianCalendar date2){
        if( (Helper.convertDate(date1)+ first.duration + this.gap) <= (Helper.convertDate(date2))){
            return true;
        }
        return false;
    }

    public String toString(){
        return first.name + " is before " + second.name + " with a gap of " + this.gap + " minutes";
    }
}

class MeetConstraint extends BinaryConstraint{
    public MeetConstraint(Activity a, Activity b){
        super(a, b);
    }

    protected boolean isSatisfied(GregorianCalendar date1, GregorianCalendar date2){
        if((Helper.convertDate(date1) + first.duration) == (Helper.convertDate(date2))){
            return true;
        }
        return false;
    }

    public String toString(){
        return first.name + " ends exactly as " + second.name + " is starting";
    }
}

class MaxSpanConstraint implements Constraint{
    protected ArrayList<Activity> activities;
    protected int span; // in minutes

    public MaxSpanConstraint(ArrayList<Activity> act, int a){
        activities = act;
        span = a;
    }

    public boolean isSatisfiedBySchedule(HashMap<Activity, GregorianCalendar> edt){
        int dmin = Helper.convertDate(edt.get(activities.get(0)));
        int dmax = (Helper.convertDate(edt.get(activities.get(0))) + activities.get(0).duration);

        for (Activity temp : activities) {
            int activityDate = Helper.convertDate(edt.get(temp));

            if(dmin > activityDate){
                dmin = activityDate;
            }
            if(dmax < (activityDate + temp.duration)){
                dmax = activityDate + temp.duration;
            }
        }

        if((dmax - dmin) <= span){
            return true;
        }
        return false;
    }

    public String toString(){
        return "The activities take up " + this.span + " minutes";
    }
}

class Verifier{
    protected ArrayList<Constraint> constraints;

    public Verifier(){
        constraints = new ArrayList<>();
    }

    protected void add(Constraint a){
        this.constraints.add(a);
    }

    protected boolean verify(HashMap<Activity, GregorianCalendar> edt){
        int result = 0;

        for (Constraint c : constraints){
            if(c.isSatisfiedBySchedule(edt)){
                result += 1;
            }
        }

        if(result == constraints.size()){
            return true;
        }
        return false;
    }
}

class RandomScheduler{
    protected Set<Activity> activities;
    protected List<Constraint> constraints;
    protected Random randomGenerator;

    public RandomScheduler(){
        activities = new HashSet<>();
        constraints = new ArrayList<>();
        randomGenerator = new Random();
    }

    protected void add(Activity a){
        activities.add(a);
    }

    protected void add(Constraint c){
        constraints.add(c);
    }

    protected HashMap<Activity, GregorianCalendar> generateTimetable(){
        HashMap<Activity, GregorianCalendar> edt = new HashMap<Activity, GregorianCalendar>();

        for(Activity act : activities){
            GregorianCalendar date = new GregorianCalendar(2019, 11, 26, randomGenerator.nextInt(24), randomGenerator.nextInt(60));
            edt.put(act, date);
        }

        return edt;
    }

    protected int testTimetable(HashMap<Activity, GregorianCalendar> edt){
        int result = 0;

        for (Constraint c : constraints){
            if(c.isSatisfiedBySchedule(edt)){
                result += 1;
            }
        }

        return result;
    }

    protected HashMap satisfiedTimetable(int n){
        HashMap<HashMap<Activity, GregorianCalendar>, Integer> timetables = new HashMap<HashMap<Activity, GregorianCalendar>, Integer>();

        HashMap<Activity, GregorianCalendar> result = new HashMap<Activity, GregorianCalendar>();
        int max;

        for(int i = 0; i < n; i++){
            HashMap<Activity,GregorianCalendar> edt = generateTimetable();
            int constraintsSatisfied = testTimetable(edt);

            timetables.put(edt, constraintsSatisfied);
        }

        max = Collections.max(timetables.values());

        for (Map.Entry<HashMap<Activity, GregorianCalendar>, Integer> entry : timetables.entrySet()) {
            if (entry.getValue().equals(max)) {
                result = entry.getKey();
            }
        }

        return result;
    }
}

class Reader{
    protected Map<String, Activity> activities;
    protected List<ConstraintDescription> constraints;

    public Reader(String pathFileA, String pathFileC) {
        activities = new HashMap<String, Activity>();
        ActivityReader actreader = new ActivityReader(pathFileA);

        constraints = new ArrayList<ConstraintDescription>();
        ConstraintReader constreader = new ConstraintReader(pathFileC);

        try{
            Map<String, ActivityDescription> fileActivities = actreader.readAll();

            for(Map.Entry<String, ActivityDescription> entry : fileActivities.entrySet()){
                activities.put(entry.getKey(), new Activity(entry.getValue().getName(), entry.getValue().getDuration()));
            }

            constraints = constreader.readAll();
        }
        catch(Exception e){
            System.out.println(e);
            System.out.println(e);
        }
    }

    protected ArrayList<Activity> getActivities(){
        ArrayList<Activity> result = new ArrayList<Activity>();

        for(Map.Entry<String, Activity> entry : activities.entrySet()){
            result.add(entry.getValue());
        }

        return result;
    }

    protected PrecedenceConstraint getPrecedenceConstraint(String[] c){
        try{
            return new PrecedenceConstraint(activities.get(c[0]), activities.get(c[1]));
        }
        catch(Exception e){
            System.out.println("Wrong arguments given.\n");
            System.out.println(e);
        }
        finally{
            return new PrecedenceConstraint(activities.get(c[0]), activities.get(c[1]));
        }
    }

    protected PrecedenceConstraintWithGap getPrecedenceConstraintWithGap(String[] c){
        try{
            return new PrecedenceConstraintWithGap(activities.get(c[1]), activities.get(c[2]), Integer.parseInt(c[0]));
        }
        catch(Exception e){
            System.out.println("Wrong arguments given.\n");
            System.out.println(e);
        }
        finally{
            return new PrecedenceConstraintWithGap(activities.get(c[1]), activities.get(c[2]), Integer.parseInt(c[0]));
        }
    }

    protected MeetConstraint getMeetConstraint(String[] c){
        try{
            return new MeetConstraint(activities.get(c[0]), activities.get(c[1]));
        }
        catch(Exception e){
            System.out.println("Wrong arguments given.\n");
            System.out.println(e);
        }
        finally{
            return new MeetConstraint(activities.get(c[0]), activities.get(c[1]));
        }
    }

    protected MaxSpanConstraint getMaxSpanConstraint(String[] c){
            ArrayList<Activity> acts = new ArrayList<Activity>();
            int span = Integer.parseInt(c[0]);

            for(String thing : c){
                for(Map.Entry<String, Activity> entry : activities.entrySet()){
                    if(thing.equals(entry.getKey())){
                        acts.add(entry.getValue());
                    }
                }
            }

        try{
            return new MaxSpanConstraint(acts, span);
        }
        catch(Exception e){
            System.out.println("Wrong arguments given.\n");
            System.out.println(e);
        }
        finally{
            return new MaxSpanConstraint(acts, span);
        }
    }

    protected ArrayList<Constraint> readConstraints(){
        ArrayList<Constraint> result = new ArrayList<Constraint>();
        for(ConstraintDescription description : constraints){
            String type = description.getKeyword();
            String[] args = description.getArguments();

            if(type.equals("PRECEDENCE")){
                result.add(getPrecedenceConstraint(args));
            }
            else if(type.equals("PRECEDENCE_GAP")){
                result.add(getPrecedenceConstraintWithGap(args));
            }
            else if(type.equals("MEET")){
                result.add(getMeetConstraint(args));
            }
            else if(type.equals("MAX_SPAN")){
                result.add(getMaxSpanConstraint(args));
            }
            else{
                System.out.println("I don't know this constraint: " + type + "\n");
            }
        }

        return result;
    }
}