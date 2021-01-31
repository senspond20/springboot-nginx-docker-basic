# Springboot + nginx 연동 docker 이미지 만들기
+ VSCODE
+ SpringBoot Maven
+ Mustache
+ Nginx
+ Docker

### pom.xml 에 다음추가

```xml

	<properties>
		<java.version>1.8</java.version>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<docker.image.prefix>docker-springboot</docker.image.prefix>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mustache</artifactId>
		</dependency>
    </dependencies>

	<build>
		<finalName>${project.name}-springboot</finalName>
		<plugins>
			<plugin>
				<groupId>com.spotify</groupId>
				<artifactId>dockerfile-maven-plugin</artifactId>
				<version>1.4.9</version>
				<configuration>
					<repository>${docker.image.prefix}/${project.artifactId}</repository>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>
```
+ MustacheConfiguation

```java
package com.example.demodock.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MustacheConfiguration implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
      
      MustacheViewResolver resolver = new MustacheViewResolver();
      resolver.setOrder(1);
      resolver.setCharset("UTF-8");
      resolver.setContentType("text/html;charset=UTF-8");
      resolver.setPrefix("classpath:/templates");
      resolver.setSuffix(".mustache");
      resolver.setCache(false); // 배포시에는 true
      
      registry.viewResolver(resolver);
      WebMvcConfigurer.super.configureViewResolvers(registry);
  }
}

```

+ HomeController
  
```java
package com.example.demodock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("")
    public String home(Model m){
        m.addAttribute("content", "2021년 새해에는 좋은일이 가득하시길 !!");
        return "index";
    }
}
```

+ Dockerfile

```bash
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/demodock-springboot.jar
COPY ${JAR_FILE} docker-springboot.jar
WORKDIR docker-springboot
EXPOSE 8083
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/docker-springboot.jar"]
```

+ nginx/conf.d/nginx.conf

```bash
user  nginx;
worker_processes  1;
error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;
events {                     
    worker_connections  1024;
}
http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;
    upstream spring-boot {
        server docker-springboot:8083;
    }
    server {
        listen 80;
        server_name localhost;
        location / {
            proxy_pass         http://spring-boot;
            proxy_redirect     off;
            proxy_set_header   Host $host;
            proxy_set_header   X-Real-IP $remote_addr;
            proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;          
        }
    }
    
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';
    access_log  /var/log/nginx/access.log  main;
                                                
    sendfile        on;                                                                         
    keepalive_timeout  65;                                                                      
    include /etc/nginx/conf.d/*.conf;     
}
```

+ docker-compose.yml

```yml
version: "3"
services:
  nginx:
    container_name: nginx_forboot
    image: nginx:alpine
    restart: unless-stopped
    ports:
      - 80:80
    volumes:
      - ./nginx/conf.d/nginx.conf:/etc/nginx/nginx.conf # nginx 설정 파일 volume 맵핑
  docker-springboot:
    container_name: springboot_test
    build: .
    expose:
      - 8083 
```

+ docker 이미지 올리기
```
docker-compose up --build -d
```
