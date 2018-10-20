package br.com.bittrexanalizer.api;

import br.com.bittrexanalizer.domain.EntidadeDomain;

public class ApiCredentials extends EntidadeDomain{

	private Long id;
    private String key;
    private String secret;

    public ApiCredentials() {
    }

    public ApiCredentials(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
