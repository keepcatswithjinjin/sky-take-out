package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /***
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);


    /***
     * 分页查询 返回总数和信息数组
     * 数据使用集合方式
     * @return
     */
    PageResult page(String name, int page, int pageSize);


    /***
     * 状态转换
     * @param status
     * @param id
     */
    void status(int status, int id);

    /***
     * id查人
     * @param id
     */
    Employee selectById(int id);


    /***
     * 修改员工信息
     * @param employeeDTO
     */
    void fix(EmployeeDTO employeeDTO);


    /***
     * 修改密码
     * @param passwordEditDTO
     */
    void passwordEditById(PasswordEditDTO passwordEditDTO);
}
