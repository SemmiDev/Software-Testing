package com.sammidev.student;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends CrudRepository<Student, UUID> {

    @Query(value = "select id, name, nim, email, phone from student where phone = :phone", nativeQuery = true)
    Optional<Student> selectStudentByPhoneNumber(@Param("phone") String phone);

    @Query(value = "select id, name, nim, email, phone from student where nim = :nim", nativeQuery = true)
    Optional<Student> selectStudentByNIM(@Param("nim") String nim);

    @Query(value = "select id, name, nim, email, phone from student where email = :email", nativeQuery = true)
    Optional<Student> selectStudentByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "delete from Student s where s.email = :email")
    void deleteStudentByEmail(@Param("email") String email);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update Student student set student.name = :name where student.nim = :nim")
    void updateStudentByNIM(@Param("name") String name ,@Param("nim") String nim);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update Student student set" +
            " student.name = :name," +
            " student.phone = :phone," +
            " student.email = :email where student.nim = :nim")
    void updateNamePhoneEmailByNIM(
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("nim") String nim);
}