package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation("员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }


    /***
     * 新增
     *
     */
    @ApiOperation("新增员工")
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工{}",employeeDTO);

        employeeService.save(employeeDTO);
        return Result.success();
    }


    /***
     * 员工分页查询
     * 根据名字查询   模糊匹配
     */
    @ApiOperation("分页查询员工")
    @GetMapping("/page")
    public Result page(String name, int page, int pageSize){
        log.info("进行分页查新 name：{}  page：{}  pageSize：{}",name,page,pageSize);
         PageResult pageResult = employeeService.page(name,page,pageSize);

        return Result.success(pageResult);
    }


    /***
     * 根据id查询员工  精准查询
     */
    @ApiOperation("Id查询员工")
    @GetMapping("/{id}")
    public Result selectById(@PathVariable int id){
         Employee empList = employeeService.selectById(id);
        return Result.success(empList);
    }

    /***
     * 员工状态
     */
    @ApiOperation("员工状态转换")
    @PostMapping("/status/{status}")
    public Result status(@PathVariable int status, int id){
        log.info("员工id:{}   状态:{}",id,status);
        employeeService.status(status,id);
        return Result.success();
    }


    /***
     * 编辑员工
     */
    @ApiOperation("编辑员工信息")
    @PutMapping
    public Result fix(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息：{}",employeeDTO);
        employeeService.fix(employeeDTO);
        return Result.success();

    }


    /***
     *  修改密码
     */
    @ApiOperation("修改密码")
    @PutMapping("/editPassword")
    public Result passwordEdit(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("修改密码操作：{}",passwordEditDTO);
        employeeService.passwordEditById(passwordEditDTO);
        return Result.success();
    }


    /**
     * 退出
     *
     * @return
     */
    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

}
