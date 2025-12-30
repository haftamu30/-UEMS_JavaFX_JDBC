module UEMS {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires javafx.base;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    // Use the correct package name based on your project's directory structure
    opens employeemanagementsystem.controller to javafx.fxml;
    opens employeemanagementsystem.model to javafx.base;

    exports employeemanagementsystem;
    exports employeemanagementsystem.controller;
    exports employeemanagementsystem.model;
    exports employeemanagementsystem.service;
    exports employeemanagementsystem.util;
    exports employeemanagementsystem.dao;
}