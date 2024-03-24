package com.sky.mapper;

import com.sky.dto.PasswordEditTransDto;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("insert into employee(name, username, phone, sex, id_number, create_time, update_time, create_user, update_user,status,password) " +
            "values (#{name},#{username},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status},#{password})")
    void save(Employee employee);


    /***
     * 根据用户id查询 密码修改
     */
    @Select("select * from employee where id = #{empId}")
    Employee fixPasswordByID(Long empId);

    /**
     * 根据姓名分页查询员工信息
     * @return
     */
    //xml
    List<Employee> list(String name);

    /***
     *
     * @param status
     * @param id
     */
    @Update("update employee set status = #{status} where id = #{id} ")
    void status(int status, int id);


    /**
     * id查人
     * @param id
     */
    @Select("select * from employee where id = #{id}")
    Employee selectById(int id);


    /***
     *
     * @param employee
     */
    void fix(Employee employee);


    /***
     * 修改密码
     * @param passwordEditTransDto
     */

    @Update("update employee set password = #{newPassword},create_time = #{createTime},create_user=#{createUser},update_time=#{updateTime},update_user=#{updateUser} where id = #{empId}")
    void passwordEditById(PasswordEditTransDto passwordEditTransDto);
}
