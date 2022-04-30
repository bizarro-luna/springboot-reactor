package com.microservicios.springboot.flux.app.modelo;

public class UsuarioComentario {
	
	
	private Usuario usuario;
	
	
	private Comentarios comentarios;


	public UsuarioComentario(Usuario usuario, Comentarios comentarios) {
		this.usuario = usuario;
		this.comentarios = comentarios;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UsuarioComentario [usuario=");
		builder.append(usuario);
		builder.append(", comentarios=");
		builder.append(comentarios);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
	

}
