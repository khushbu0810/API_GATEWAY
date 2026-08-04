package com.example.account_mgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AccountMgmtApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountMgmtApplication.class, args);
	}

}
