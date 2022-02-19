# Fil Rouge
Homework for the OOP class.

# Members of the group
* MERZOUGUI Dhia Eddine 21812920 (G2B)  
* MERCIER Julien 21802414 (G2B)  
* Mamadou Galle Diallo 21513751 (G2B)  
* HEIDARI Shahin 21911577 (G2B)  

Prof chargé du TP :  M.Olivier RANAIVOSON  
Séance: Vendredi matin 8h-11h30  

# Usage

* Put the scheduleio.jar file in the root of the project folder

* "Main" is the executable class

## How to add activities:
To add activities, write them in the *activities.txt* file in this format:  
```
<identifier> <length> <description>  
Ex : cafe 5 prendreUnCafe  
```

## How to add constraints:
Write them in the *constraints.txt* file in this format:  
```
<Keyword> <(x  minutes depending on the constraint)> <first_activity> <second_activity> ..(some more if MaxSpanConstraint)  
Ex : MAX_SPAN 60 cafe bus salut  
```

## Keywords
```
PRECEDENCE : PrecedenceConstraint (One activity after another)  
PRECEDENCE_GAP : PrecedenceConstraintWithGap (one activity after another with an *x* minutes gap)  
MEET : MeetConstraint  (one activity starts exactly when the other one finishes)  
MAX_SPAN : MaxSpanConstraint  (a group of activities taking *x* minutes to be finished)  
```

## Options

###### For option 1
* p : Generate a timetable that forcibly respects all constraints  
* np : Generate a timetable that respects most constraints but not all

###### For option 2
* c : Print the timetable in a chronological order  
* np : Print the timetable in an ordinary order  

# Compilation  

#### For MacOS / Linux
Compilation : `javac -cp ".:scheduleio.jar" -d ./build activite/Main.java`  
Execution : `java -cp "build:scheduleio.jar" activite.Main activities.txt constraints.txt <option1> <option2>`  

#### For Windows
Compilation : `javac -cp ".;scheduleio.jar" -d ./build activite/Main.java`  
Execution : `java -cp "build;scheduleio.jar" activite.Main activities.txt constraints.txt <option1> <option2>`  

