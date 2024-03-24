package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.dto.PasswordEditTransDto;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // MD5加密后的密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee  = new Employee();
        /***
         * BeanUtils.copyProperties(employeeDTO,employee);
         * 把dto里的数据copy到employee中
         * dto在传输时减少资源浪费  处理时进行转换
         */
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setStatus(StatusConstant.ENABLE);
        /***
         *md5加密
         */
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

//        if(employee.getSex().equals("男")){
//            employee.setSex("1");
//        }else if(employee.getSex().equals("女")){
//            employee.setSex("2");
//        }

        //获取当前登录用户id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        /***
         * 校验username是否唯一
         * 捕获异常来返回提醒
         */
        employeeMapper.save(employee);

    }


    /***
     * 分页查询
     * @return
     */
    @Override
    public PageResult page(String name, int page, int pageSize) {
        //1.设置分页参数
        PageHelper.startPage(page,pageSize);

        //2.普通条件查询即可  页数由插件完成
         List<Employee> empList = employeeMapper.list(name);
        //3.封装
        //强转pageHelper类型
        Page<Employee> p = (Page<Employee>) empList;
        //封装
        PageResult pageResult = new PageResult(p.getTotal(), p.getResult());
        return pageResult;
    }

    /***
     * 状态转换
     * @param status
     * @param id
     */
    @Override
    public void status(int status, int id) {
        employeeMapper.status(status,id);
    }


    /***
     * id查人
     * @param id
     */
    @Override
    public Employee selectById(int id) {
        Employee empList = employeeMapper.selectById(id);
        return empList;
    }


    /***
     * 更新用户
     * @param employeeDTO
     */
    @Override
    public void fix(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        //加入更新信息
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.fix(employee);


    }


    /***
     * 修改密码
     * @param passwordEditDTO
     */

    //TODO  修改密码功能接口测试通过 但前后端联调失败，前端无法传回empId 初步认为是前端代码问题 之后再解决

    @Override
    public void passwordEditById(PasswordEditDTO passwordEditDTO) {
        PasswordEditTransDto passwordEditTransDto = new PasswordEditTransDto();
        BeanUtils.copyProperties(passwordEditDTO,passwordEditTransDto);
        //加入更新信息
        /***
         *    修改新密码
         */


        //利用id返回数据库中的信息
        log.info("利用id返回数据库中的密码信息");
        Employee employee = employeeMapper.fixPasswordByID(passwordEditTransDto.getEmpId());

        //密码比对
        // MD5加密后的密码与数据库中密码进行比较
         String oldPassword = DigestUtils.md5DigestAsHex(passwordEditTransDto.getOldPassword().getBytes());
         String newPassword = DigestUtils.md5DigestAsHex(passwordEditTransDto.getNewPassword().getBytes());

         if (!oldPassword.equals(employee.getPassword())) {
            //如果旧密码与数据库中密码不相同  抛出异常
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR_OLD);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        if(newPassword.equals(oldPassword)){
            throw new AccountLockedException(MessageConstant.PASSWORD_ERROR_EQ);
        }


        //没有问题则进行密码修改

        passwordEditTransDto.setUpdateTime(LocalDateTime.now());
        passwordEditTransDto.setCreateTime(LocalDateTime.now());
        passwordEditTransDto.setUpdateUser(BaseContext.getCurrentId());
        passwordEditTransDto.setCreateUser(BaseContext.getCurrentId());
        passwordEditTransDto.setNewPassword(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()));
        employeeMapper.passwordEditById(passwordEditTransDto);

    }

}
