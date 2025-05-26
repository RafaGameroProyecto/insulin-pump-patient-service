# 👥 Patient Service

Microservicio para la gestión de pacientes diabéticos en el sistema de bombas de insulina.

## 🚀 Descripción

El Patient Service es responsable de gestionar toda la información relacionada con los pacientes diabéticos, incluyendo sus datos médicos, contactos de emergencia, tipo de diabetes y asignación de dispositivos.

## 🛠️ Tecnologías

- Java 21  
- Spring Boot 3.4.5  
- Spring Data JPA  
- MySQL 8.0  
- Spring Cloud Netflix Eureka  
- OpenFeign  
- Lombok  
- Bean Validation  

## 📋 Funcionalidades

### ✅ Gestión de Pacientes
- Registro de nuevos pacientes  
- Actualización de información médica  
- Consulta por ID, ID médico o dispositivo asignado  
- Búsqueda por nombre y criterios médicos  

### ✅ Gestión de Dispositivos
- Asignación de bombas de insulina a pacientes  
- Desasignación de dispositivos  
- Consulta de pacientes por dispositivo  

### ✅ Información Médica
- Tipos de diabetes (Tipo 1, Tipo 2, Gestacional)  
- Niveles de HbA1c  
- Historial de diagnóstico  
- Contactos de emergencia  

## 🌐 Endpoints Principales

### Acceso Directo (Puerto 8081)

| Método | Endpoint                              | Descripción                    |
|--------|---------------------------------------|--------------------------------|
| GET    | /api/patients                         | Obtener todos los pacientes    |
| GET    | /api/patients/{id}                    | Obtener paciente por ID        |
| GET    | /api/patients/medical/{medicalId}     | Buscar por ID médico           |
| POST   | /api/patients                         | Crear nuevo paciente           |
| PUT    | /api/patients/{id}                    | Actualizar paciente            |
| PUT    | /api/patients/{patientId}/device/{deviceId} | Asignar dispositivo     |
| DELETE | /api/patients/{id}                    | Eliminar paciente              |

### Acceso a través de Gateway (Puerto 8087) - RECOMENDADO

| Método | Endpoint Gateway                                        | Descripción                 |
|--------|----------------------------------------------------------|-----------------------------|
| GET    | http://localhost:8087/api/patients                      | Obtener todos los pacientes |
| GET    | http://localhost:8087/api/patients/{id}                | Obtener paciente por ID     |
| GET    | http://localhost:8087/api/patients/medical/{medicalId} | Buscar por ID médico        |
| POST   | http://localhost:8087/api/patients                     | Crear nuevo paciente        |

## 🗄️ Modelo de Datos

```java
@Entity
public class Patient {
    private Long id;
    private String name;
    private Integer age;
    private String medicalId;
    private Long deviceId;
    private String diabetesType;
    private LocalDate dateOfBirth;
    private String email;
    private String phone;
    private String address;
    private String emergencyContact;
    private LocalDate diagnosisDate;
    private String insulinType;
    private Float hba1cLevel;
    private Boolean isActive;
}
```

## ⚙️ Configuración

### Puerto

```
server.port=8081
```

### Base de Datos

```
spring.datasource.url=jdbc:mysql://localhost:3306/pacientes
spring.datasource.username=root
spring.datasource.password=****
```

### Eureka

```
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

## 🚀 Ejecución

1. Iniciar MySQL en puerto 3306  
2. Iniciar Eureka Server en puerto 8761  
3. Iniciar Gateway Service en puerto 8087  
4. Ejecutar la aplicación:



### Verificar

- Directo: http://localhost:8081/api/patients  
- Gateway: http://localhost:8087/api/patients  



## 🔗 Comunicación con Otros Servicios

### Device Service
- Consulta información de dispositivos  
- Sincronización de asignaciones  

### Reading Service
- Proporciona información de pacientes para lecturas  
- Validación de pacientes existentes  

### Gateway Service
- Enrutamiento automático de peticiones  
- Balanceador de carga  
- Logging centralizado  

## 👨‍💻 Autor

**Rafael Gamero Arrabal**  
[🔗 LinkedIn](https://www.linkedin.com/in/rafael-gamero-arrabal-619200186/)
