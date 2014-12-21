package com.tak3r07.CourseStatistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tak3r07 on 11/9/14.
 */
public class Course implements Serializable {
    static final long serialVersionUID = 2099962292244075360L;

    //Coursename
    private String courseName;

    //List of the course assignments
    private ArrayList<Assignment> mAssignmentArrayList = new ArrayList<Assignment>();


    //Number of how many assignments there are
    private int numberOfAssignments;
    private Double reachablePointsPerAssignment;


    public Course(String courseName) {

        //Sets course name
        setCourseName(courseName);

        //usually 13 assignments in one semester
        numberOfAssignments = 13;

        //Set reachablePointsPerAssignment
        reachablePointsPerAssignment = 100d;

    }


    //Constructor
    public Assignment getAssignment(int index) {
        try {
            return mAssignmentArrayList.get(index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Add new Course
    public void addAssignment(Assignment assignment) {
        mAssignmentArrayList.add(assignment);
    }

    //Calculate overall percentage of current assignments (= average)
    public Double getOverAllPercentage() {
        double overAllAchievedPoints = 0;
        double overAllMaxPoints = 0;


        //Iterate on the array and sum up all its max and achieved points
        Assignment currentAssignment;
        for (Iterator<Assignment> iterator = mAssignmentArrayList.iterator(); iterator.hasNext(); ) {
            currentAssignment = iterator.next();
            overAllAchievedPoints += currentAssignment.getAchievedPoints();
            overAllMaxPoints += currentAssignment.getMaxPoints();
        }

        //Round on 4 digits
        double overAllPercentage = Math.round(overAllAchievedPoints / overAllMaxPoints * 1000) / 10d;


        //return result
        return overAllPercentage;

    }

    /**
     * calculate average : (all points of current assignments) / ((max points per assignment) * (number of assignments))
     *
     */
    public Double getAverage() {
        double overAllAchievedPoints = 0;

        //Iterate on the array and sum up all its max and achieved points
        Assignment currentAssignment;
        for (Iterator<Assignment> iterator = mAssignmentArrayList.iterator(); iterator.hasNext(); ) {

            overAllAchievedPoints += iterator.next().getAchievedPoints();

        }

        //Round on 4 digits

        double average = Math.round(overAllAchievedPoints / (reachablePointsPerAssignment * numberOfAssignments) * 1000) / 10d;

        //return result
        return average;
    }


    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public ArrayList<Assignment> getAssignments() {
        return mAssignmentArrayList;
    }

    public void setAssignments(ArrayList<Assignment> newAssignments) {
        mAssignmentArrayList = newAssignments;
    }

    public int getNumberOfAssignments() {
        return numberOfAssignments;
    }

    public void setNumberOfAssignments(int numberOfAssignments) {
        this.numberOfAssignments = numberOfAssignments;
    }

    public Double getReachablePointsPerAssignment() {
        return reachablePointsPerAssignment;
    }

    public void setReachablePointsPerAssignment(Double reachablePointsPerAssignment) {
        this.reachablePointsPerAssignment = reachablePointsPerAssignment;

        //Set in each Assignment
        Assignment currentAssignment;
        for (Iterator<Assignment> it = mAssignmentArrayList.iterator(); it.hasNext(); ) {

            currentAssignment = it.next();
            if (currentAssignment.isExtraAssignment() == false) {
                currentAssignment.setMaxPoints(reachablePointsPerAssignment);
            }
        }
    }

    /**
     * Simple Clone code (deep copy)
     */
    public Course clone() {
        Course clone = new Course(courseName);
        clone.setReachablePointsPerAssignment(reachablePointsPerAssignment);
        clone.setNumberOfAssignments(numberOfAssignments);

        ArrayList<Assignment> cloneList = new ArrayList<Assignment>();
        for (Assignment a : mAssignmentArrayList) {
            cloneList.add(a);
        }

        return clone;
    }

    /**
     * Return necessary points per assignment until 50% reached
     */
    public Double getNecessaryPointsPerAssignmentUntilFin() {

        //Necessary points for the whole course to reach 50%
        Double necPointsAtAll = numberOfAssignments * reachablePointsPerAssignment * 0.5;

        //yet achieved points
        Double achievedPointsAtAll = getTotalPoints();

        //Number of assignments left for this semester
        int numberAssignmentsLeft = numberOfAssignments - mAssignmentArrayList.size();


        Double numberOfPointsLeft = necPointsAtAll - achievedPointsAtAll;

        //Missing points divided by missing assignments
        Double necPointsPerAssUntilFin = Math.round(numberOfPointsLeft / numberAssignmentsLeft * 100) / 100d;


        if (necPointsPerAssUntilFin < 0) return 0.;

        return necPointsPerAssUntilFin;

    }

    /**
     * Return the number of assignments which are necessary until one would reach 50% with its current performance per assignment
     */
    public int getNumberOfAssUntilFin() {

        //Necessary points for the whole course to reach 50%
        Double necPointsAtAll = numberOfAssignments * reachablePointsPerAssignment * 0.5;

        //Yet achieved points
        Double achievedPointsAtAll = getTotalPoints();

        //Average points per assignment
        Double averagePointsPerAssignment = getAveragePointsPerAssignment();

        //Scenario: Course has been initialized for the first time
        if (averagePointsPerAssignment == 0) return 0;

        //Predicted points if you get your average points in all your next assignments
        Double predictedPoints = achievedPointsAtAll;

        //Counts the number of assignments
        int count = 0;

        //Each loop adds the averagepoints per assignment so it predicts your future results
        while (predictedPoints < necPointsAtAll) {
            count++;
            predictedPoints += averagePointsPerAssignment;
        }

        return count;
    }

    /**
     * Returns Average Points per Assignment
     */
    public Double getAveragePointsPerAssignment() {
        return Math.round(getOverAllPercentage() * getReachablePointsPerAssignment() * 1) / 100d;
    }

    /**
     * Returns total points of all assignments of this course
     *
     * @return achievedPointsAtAll Double
     */
    public Double getTotalPoints() {

        //Start sum at 0 points
        Double achievedPointsAtAll = 0.;

        //Iterate on the array and sum up all its achieved points
        for (Iterator<Assignment> iterator = mAssignmentArrayList.iterator(); iterator.hasNext(); ) {
            achievedPointsAtAll += iterator.next().getAchievedPoints();
        }

        return achievedPointsAtAll;
    }

}
