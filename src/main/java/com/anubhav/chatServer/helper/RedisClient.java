package com.anubhav.chatServer.helper;

import redis.clients.jedis.Jedis;

public class RedisClient {
	
	private Jedis jedis=null;
	
	public RedisClient(String redisHost , int port) {
		jedis = new Jedis(redisHost , port);
	}
	
	public void set(String key , String value) {
		jedis.set(key, value);
	}
	
	public String get(String key) {
		return jedis.get(key);
	}
	

}
