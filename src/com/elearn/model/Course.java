package com.elearn.model;

public class Course {
    private int courseId;
    private String title;
    private String description;
    private String instructor;
    private String imagePath;

        public Course(int courseId, String title, String description, String instructor) {
            this.courseId = courseId;
            this.title = title;
            this.description = description;
            this.instructor = instructor;
        }

        public Course() { }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    // Video support removed — no video URL fields



    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
