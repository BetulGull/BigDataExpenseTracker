package com.example.demo.Controller;

import com.example.demo.DTO.EmployeeExpenseDTO;
import com.example.demo.DTO.ExpenseDTO;
import com.example.demo.Model.Department;
import com.example.demo.Model.Employee;
import com.example.demo.Service.DepartmentService;
import com.example.demo.Service.EmployeeService;
import com.example.demo.Service.ExpenseService;
import com.example.demo.Service.ImageService;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    private final ImageService imageService;
    private final DepartmentService departmentService;
    @Autowired
    private SparkSession sparkSession;

    @Autowired
    public EmployeeController(EmployeeService employeeService,  ImageService imageService, DepartmentService departmentService) {
        this.employeeService = employeeService;

        this.imageService = imageService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String listEmployees(Model model) {
        // Step 1: Fetch all employees from the MySQL database
        List<Employee> employees = employeeService.getAllEmployees();
        System.out.println("Fetched employees from database: " + employees);

        // Step 2: Fetch total expenses for employees from Cassandra using Spark
        Map<Long, Double> expenseData = fetchEmployeeExpenses();

        // Step 3: Update each employee's expense field with data from Cassandra
        for (Employee employee : employees) {
            Double expense = expenseData.getOrDefault(employee.getUserId(), 0.0);
            employee.setExpense(expense);
        }

        // Step 4: Add the enriched employees list to the model
        model.addAttribute("employees", employees);

        // Step 5: Return the view name
        return "employees";
    }

    private Map<Long, Double> fetchEmployeeExpenses() {
        return sparkSession.read()
                .format("org.apache.spark.sql.cassandra")
                .options(Map.of("keyspace", "demo", "table", "expenses"))
                .load()
                .limit(1000) // Fetch only a limited number of rows for testing
                .groupBy("user_id")
                .agg(org.apache.spark.sql.functions.sum("payment").alias("expense"))
                .collectAsList()
                .stream()
                .collect(Collectors.toMap(
                        row -> row.getLong(0), // user_id
                        row -> row.getDecimal(1).doubleValue() // Convert BigDecimal to Double
                ));
    }








    // Çalışan ekleme formunu gösterme
    @GetMapping("/create")
    public String showCreateEmployeeForm(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", departments);
        return "addEmployee";
    }

    // Çalışan düzenleme formunu gösterme
    @GetMapping("/edit/{empno}")
    public String showEditEmployeeForm(@PathVariable Long empno, Model model) {
        Employee employee = employeeService.getEmployeeById(empno);
        if (employee == null) {
            return "redirect:/employees";
        }
        model.addAttribute("employee", employee);
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        return "editEmployee";
    }
    @Autowired
    private ExpenseService expenseService;


    @GetMapping("/updateExpense")
    public String updateEmployeeExpenses() {
        List<Employee> employees = employeeService.getAllEmployees();

        for (Employee employee : employees) {
            // Fetch sum of payments from Cassandra based on user_id
            Double totalExpense = employeeService.getTotalExpenseFromCassandra(employee.getUserId());
            employee.setExpense(totalExpense);
            employeeService.updateEmployee(employee);  // Update employee's expense
        }
        employees.forEach(employee ->
                System.out.println("Employee: " + employee.getUserId() + ", Expense: " + employee.getExpense())
        );

        return "redirect:/employees"; // Redirect to employee list after updating expenses
    }

    // Çalışan ekleme işlemi
    @PostMapping("/add")
    public String addEmployee(@ModelAttribute Employee employee,
                              @RequestParam("img") MultipartFile imgFile) {
        try {
            // İmaj dosyası yükleniyorsa
            if (imgFile != null && !imgFile.isEmpty()) {
                String imageName = imgFile.getOriginalFilename();
                imageService.uploadImageToHDFS(imageName, imgFile);
                employee.setImgName(imageName);
            }

            // Departman kontrolü
            if (employee.getDepartment() != null && employee.getDepartment().getDeptno() != null) {
                Department department = departmentService.getDepartmentById(employee.getDepartment().getDeptno());
                employee.setDepartment(department);
            } else {
                logger.error("Department is not set for employee");
                return "redirect:/employees";  // Hatalı departman durumu
            }

            // Çalışanı veritabanına kaydet
            employeeService.saveEmployee(employee);
        } catch (IOException e) {
            logger.error("Error uploading image", e);
            return "addEmployee";  // İmaj yükleme hatası durumunda
        }
        return "redirect:/employees";  // Başarılı ekleme sonrası
    }

    // Çalışan güncelleme işlemi
    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute Employee employee,
                                 @RequestParam("img") MultipartFile imgFile) {
        try {
            // İmaj dosyası yükleniyorsa
            if (imgFile != null && !imgFile.isEmpty()) {
                String imageName = imgFile.getOriginalFilename();
                imageService.uploadImageToHDFS(imageName, imgFile);
                employee.setImgName(imageName);
            } else {
                // İmaj dosyası yüklenmediyse mevcut imajı koru
                Employee existingEmployee = employeeService.getEmployeeById(employee.getEmpno());
                employee.setImgName(existingEmployee.getImgName());
            }

            // Departman kontrolü
            if (employee.getDepartment() != null && employee.getDepartment().getDeptno() != null) {
                Department department = departmentService.getDepartmentById(employee.getDepartment().getDeptno());
                employee.setDepartment(department);
            } else {
                logger.error("Department is not set for employee");
                return "redirect:/employees";  // Hatalı departman durumu
            }

            // Çalışanı güncelle
            employeeService.updateEmployee(employee);
        } catch (IOException e) {
            logger.error("Error uploading image", e);
            return "editEmployee";  // İmaj yükleme hatası durumunda
        }
        return "redirect:/employees";  // Başarılı güncelleme sonrası
    }

    // Çalışan silme işlemi
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployeeById(id);
        return "redirect:/employees";
    }

    // İmaj dosyasını almak için metod
    @GetMapping("/image/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Resource resource = imageService.loadImageFromHDFS(imageName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageName + "\"")
                    .body(resource);
        } catch (IOException e) {
            logger.error("Error loading image", e);
            return ResponseEntity.notFound().build();
        }
    }
}
