package br.com.bittrexanalizer.domain;

import java.math.BigDecimal;

public class Balance extends EntidadeDomain implements Comparable<Balance>{

	/*"Currency" : "DOGE",
	"Balance" : 0.00000000,
	"Available" : 0.00000000,
	"Pending" : 0.00000000,
	"CryptoAddress" : "DLxcEt3AatMyr2NTatzjsfHNoB9NT62HiF",
	"Requested" : false,
	"Uuid" : null*/

	private Long id;
	private String currency;
	private BigDecimal balance;
	private BigDecimal available;
	private BigDecimal pending;
	private String cryptoAddress;
	private Boolean requested;
	private String uuid;
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getAvailable() {
		return available;
	}
	public void setAvailable(BigDecimal available) {
		this.available = available;
	}
	public BigDecimal getPending() {
		return pending;
	}
	public void setPending(BigDecimal pending) {
		this.pending = pending;
	}
	public String getCryptoAddress() {
		return cryptoAddress;
	}
	public void setCryptoAddress(String cryptoAddress) {
		this.cryptoAddress = cryptoAddress;
	}
	public Boolean getRequested() {
		return requested;
	}
	public void setRequested(Boolean requested) {
		this.requested = requested;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Balance other = (Balance) obj;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Balance [currency=" + currency + ", balance=" + balance + ", available=" + available + ", pending="
				+ pending + ", cryptoAddress=" + cryptoAddress + ", requested=" + requested + ", uuid=" + uuid + "]";
	}
	@Override
	public int compareTo(Balance o) {
		
		int valor = 0;
		
		if(this.getAvailable().compareTo(o.getAvailable())==1){
			valor=1;
		}else if(this.getAvailable().compareTo(o.getAvailable())==-1){
			valor = -1;
		}
		return valor;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
