package com.microservicios.springboot.flux.app.modelo;

import java.util.ArrayList;
import java.util.List;

public class Comentarios {
	
	
	List<String> comentarios;

	
	

	public Comentarios() {
		this.comentarios = new ArrayList<>();
	}

	





	/**
	 * @param comentarios the comentarios to set
	 */
	public void addComentario(String comentario) {
		this.comentarios.add(comentario);
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("comentarios=");
		builder.append(comentarios);
		builder.append("");
		return builder.toString();
	}
	
	

}
