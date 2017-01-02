package com.tak3r07.CourseStatistics.utils;

import com.tak3r07.CourseStatistics.database.DatabaseHelper;
import com.tak3r07.CourseStatistics.objects.DynamicPointsCourse;
import com.tak3r07.CourseStatistics.objects.FixedPointsCourse;
import com.tak3r07.CourseStatistics.objects.Assignment;
import com.tak3r07.CourseStatistics.objects.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by tak on 8/26/15.
 * This class parses jsonstrings to courses and assignments
 */
public class JSONParser {

    /**
     * This method parses a JSON in string-format into a course
     *
     * @param jsonString course as JSON-String
     * @return Course object which is build of the given string
     */
    public static Course jsonToCourse(String jsonString) {
        try {
            JSONObject jsonCourse = new JSONObject(jsonString);

            UUID id = UUID.fromString(jsonCourse.getString(DatabaseHelper.KEY_ID));
            String courseName = jsonCourse.getString(DatabaseHelper.KEY_COURSENAME);
            int numberOfAssignments = jsonCourse.getInt(DatabaseHelper.KEY_NUMBER_OF_ASSIGNMENTS);

            //Get max points
            double maxPoints = jsonCourse.getDouble(DatabaseHelper.KEY_REACHABLE_POINTS_PER_ASSIGNMENT);

            //get index
            int index = jsonCourse.getInt(DatabaseHelper.KEY_COURSE_INDEX);

            //get necPercentToPass
            double necPercentToPass = jsonCourse.getDouble(DatabaseHelper.KEY_NEC_PERCENT_TO_PASS);

            //get date
            long date = jsonCourse.getLong(DatabaseHelper.KEY_DATE);

            //Get has fixed points (1 == true, 0 == false in sqlite)
            boolean hasFixedPoints = jsonCourse.getBoolean(DatabaseHelper.KEY_HAS_FIXED_POINTS);

            //Get assignments
            String assignmentsJSONArrayString = jsonCourse.getString("assignments");
            ArrayList<Assignment> assignments;
            assignments  = jsonArrayToAssignmentArray(assignmentsJSONArrayString);

            Course course;

            //Create specific course instance depending on "hasFixedPoints"
            if (hasFixedPoints) {
                course = new FixedPointsCourse(courseName, index, maxPoints);
            } else {
                course = new DynamicPointsCourse(courseName, index);
            }

            //Set properties
            course.setId(id);
            course.setNumberOfAssignments(numberOfAssignments);
            course.setNecPercentToPass(necPercentToPass);
            course.setDate(date);
            course.setAssignments(assignments);

            return course;


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Parse a JSONArray-String to a ArrayList of Courses
     *
     * @param jsonString
     * @return
     */

    public static ArrayList<Course> jsonArrayToCourseArray(String jsonString) {
        //Init arraylist
        ArrayList<Course> courses = new ArrayList<>();
        try {
            //Parse string to JSONArray
            JSONArray jsonArray = new JSONArray(jsonString);

            //For each JSONObject as string create a Course and add it to the list
            for (int i = 0; i < jsonArray.length(); i++) {
                String courseString = jsonArray.getString(i);
                Course course = jsonToCourse(courseString);
                courses.add(course);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * This method parses a JSONObject-String to an assignment
     *
     * @param jsonString
     * @return
     */
    public static Assignment jsonToAssignment(String jsonString) {
        try {
            JSONObject jsonAssignment = new JSONObject(jsonString);

            //Get all properties from the JSONObject
            UUID id = UUID.fromString(jsonAssignment.getString(DatabaseHelper.KEY_ID));
            int index = jsonAssignment.getInt(DatabaseHelper.KEY_ASSIGNMENT_INDEX);
            double maxPoints = jsonAssignment.getDouble(DatabaseHelper.KEY_MAX_POINTS);
            boolean isExtraAssignment = jsonAssignment.getBoolean(DatabaseHelper.KEY_IS_EXTRA_ASSIGNMENT);
            double achievedPoints = jsonAssignment.getDouble(DatabaseHelper.KEY_ACHIEVED_POINTS);
            long date = jsonAssignment.getLong(DatabaseHelper.KEY_DATE);
            UUID course_id = UUID.fromString(jsonAssignment.getString(DatabaseHelper.KEY_COURSE_ID));

            //Create assignment and set properties
            Assignment assignment = new Assignment(id, index, maxPoints, achievedPoints, course_id);
            assignment.isExtraAssignment(isExtraAssignment);
            assignment.setDate(date);

            return assignment;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method parses a JSONArray-String to an arraylist of assignments
     *
     * @param jsonString
     * @return
     */
    public static ArrayList<Assignment> jsonArrayToAssignmentArray(String jsonString) {
        //Init arraylist
        ArrayList<Assignment> assignments = new ArrayList<>();
        try {
            //Parse string to JSONArray
            JSONArray jsonArray = new JSONArray(jsonString);

            //For each JSONObject as string create an assignment and add it to the list
            for (int i = 0; i < jsonArray.length(); i++) {
                String assignmentString = jsonArray.getString(i);
                Assignment assignment = jsonToAssignment(assignmentString);
                assignments.add(assignment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return assignments;
    }

    public static JSONObject courseToJSON(Course course) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(DatabaseHelper.KEY_ID, course.getId());
            jsonObject.put(DatabaseHelper.KEY_COURSENAME, course.getCourseName());
            jsonObject.put(DatabaseHelper.KEY_NUMBER_OF_ASSIGNMENTS, course.getNumberOfAssignments());
            jsonObject.put(DatabaseHelper.KEY_REACHABLE_POINTS_PER_ASSIGNMENT, course.hasFixedPoints() ? course.toFPC().getMaxPoints() : 0);
            jsonObject.put(DatabaseHelper.KEY_COURSE_INDEX, course.getIndex());
            jsonObject.put(DatabaseHelper.KEY_HAS_FIXED_POINTS, course.hasFixedPoints());
            jsonObject.put(DatabaseHelper.KEY_NEC_PERCENT_TO_PASS, course.getNecPercentToPass());
            jsonObject.put(DatabaseHelper.KEY_DATE, course.getDate());
            JSONArray assignmentJSONArray = assignmentArrayToJSONArray(course.getAssignments());
            jsonObject.put(DatabaseHelper.KEY_ASSIGNMENTS, assignmentJSONArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject assignmentToJSON(Assignment assignment){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(DatabaseHelper.KEY_ID, assignment.getId());
            jsonObject.put(DatabaseHelper.KEY_ASSIGNMENT_INDEX, assignment.getIndex());
            jsonObject.put(DatabaseHelper.KEY_MAX_POINTS, assignment.getMaxPoints());
            jsonObject.put(DatabaseHelper.KEY_IS_EXTRA_ASSIGNMENT, assignment.isExtraAssignment());
            jsonObject.put(DatabaseHelper.KEY_COURSE_ID, assignment.getCourse_id());
            jsonObject.put(DatabaseHelper.KEY_ACHIEVED_POINTS, assignment.getAchievedPoints());
            jsonObject.put(DatabaseHelper.KEY_DATE, assignment.getDate());

        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONArray courseArrayToJSONArray(ArrayList<Course> courses){
        JSONArray array = new JSONArray();
        for(Course c : courses){
            array.put(courseToJSON(c));
        }
        return array;
    }

    public static JSONArray assignmentArrayToJSONArray(ArrayList<Assignment> assignments){
        JSONArray array = new JSONArray();
        for(Assignment a : assignments){
            array.put(assignmentToJSON(a));
        }
        return array;
    }
}
