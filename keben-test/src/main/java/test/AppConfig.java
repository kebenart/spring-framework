package test;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author Keben
 * @description
 * @date 2020-03-02 20:45
 */
public class AppConfig {
	public AppConfig(){
		System.out.println("init");
	}

	public void msg(){
		System.out.println("msg");
	}
}
