package com.microservicios.springboot.flux.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.microservicios.springboot.flux.app.modelo.Comentarios;
import com.microservicios.springboot.flux.app.modelo.Usuario;
import com.microservicios.springboot.flux.app.modelo.UsuarioComentario;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		ejemploContraPresion();
	}
	
	
	
	public void ejemploContraPresion() {
		
		 Flux.range(1, 10)
		 .log()
		 .limitRate(5)
		 .subscribe(/*new Subscriber<Integer>() {
			 
			 Subscription s;
			 
			 private Integer limite=5;
			 private Integer consumido=0;

			@Override
			public void onSubscribe(Subscription s) {
				this.s=s;
				s.request(limite);
				
			}

			@Override
			public void onNext(Integer t) {
				log.info(t.toString());
				consumido++;
				if(consumido==limite) {
					consumido=0;
					s.request(limite);
				}
				
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
		}*/  );
		
	}
	
	
	public void ejemploIntervaloDesdecreate() {
		
		Flux.create(emitter->{
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				private Integer contador=0;
				@Override
				public void run() {
					//Pre increment, para primero incrementar y despues emitir
					emitter.next(++contador);
					if(contador==10) {
						timer.cancel();
						emitter.complete();
					}
					
					
					if(contador==5) {
						timer.cancel();
						emitter.error(new InterruptedException("Error se ha terminado el timer en 5"));
						
					}
					
				}
			}, 1000,1000);
		})
//		.doOnNext(next->log.info(next.toString()))
//		.doOnComplete(()->log.info("Hemos terminado"))
		.subscribe(next->log.info(next.toString()),
				error->log.error(error.getMessage()),
				()->log.info("Hemos terminado")
				);
		
		
	}
	
	
	public void ejemploIntervaloInfinito() throws InterruptedException {
		
		
		CountDownLatch latch= new CountDownLatch(1);
		Flux.interval(Duration.ofSeconds(1))
		//.doOnTerminate(()->latch.countDown())
		.doOnTerminate(latch::countDown)
		.flatMap(i->{
			
			if(i>=5) {
				return Flux.error(new InterruptedException("Solo hasta cinco"));
			}
			
			return Flux.just(i);
			
		})
		.map(i-> "Hola "+i.toString())
		.retry(2)
		.subscribe(s->log.info(s),
				e-> log.error(e.getMessage())
				);
		
		latch.await();
		
		
	}
	
	
	public void ejemploElements() {
		Flux<Integer> rango= Flux.range(1, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i-> log.info(i.toString()));
				
				
		rango.blockLast();
		
		
				
	}
	
	
	public void ejemploInterval() {
		Flux<Integer> rango= Flux.range(1, 12);
		
		Flux<Long> retraso= Flux.interval(Duration.ofSeconds(1));
		
		
		rango.zipWith(retraso,(ra,re)->ra)
		.doOnNext(i-> log.info(i.toString()))
		.blockLast();
		
	}
	
	
	public void ejemploZipWithRange() {
		
		Flux.just(1,2,3,4)
		.map(i->(i*2))
		.zipWith(Flux.range(0, 4),(uno,dos)-> String.format("Primer Flux: %d, Segundo Flux: %d", uno,dos))
		.subscribe(texto->log.info(texto));
		
		
	}
	
	
	
	
	public void ejemploUsuarioComentarioZipWithForma2() {
		
		Mono<Usuario> usuarioMono= Mono.fromCallable(()->new Usuario("Jonh","Doe"));
		
		Mono<Comentarios> comentariosMono= Mono.fromCallable(()->{
			
			Comentarios comentarios= new Comentarios();
			
			comentarios.addComentario("Hola");
			comentarios.addComentario("mundo");
			comentarios.addComentario("que onda");
			comentarios.addComentario("whaassss");
			comentarios.addComentario("aaaaa");
			
			return comentarios;
		});
		
		
		Mono<UsuarioComentario> ucMono=  usuarioMono.zipWith(comentariosMono)
				.map(tuple->{
					Usuario u = tuple.getT1();
					Comentarios c= tuple.getT2();
					
					return new UsuarioComentario(u, c);
				});
		
		ucMono.subscribe(uc->log.info(uc.toString()));	
	}
	
	public void ejemploUsuarioComentarioZipWith() {
		
		Mono<Usuario> usuarioMono= Mono.fromCallable(()->new Usuario("Jonh","Doe"));
		
		Mono<Comentarios> comentariosMono= Mono.fromCallable(()->{
			
			Comentarios comentarios= new Comentarios();
			
			comentarios.addComentario("Hola");
			comentarios.addComentario("mundo");
			comentarios.addComentario("que onda");
			comentarios.addComentario("whaassss");
			comentarios.addComentario("aaaaa");
			
			return comentarios;
		});
		
		
		Mono<UsuarioComentario> ucMono=  usuarioMono.zipWith(comentariosMono, (usuario,comentarios)->new UsuarioComentario(usuario,comentarios));
		
		ucMono.subscribe(uc->log.info(uc.toString()));	
	}
	
	
	
	public void ejemploUsuarioComentarioFlatMap() {
		
		Mono<Usuario> usuarioMono= Mono.fromCallable(()->new Usuario("Jonh","Doe"));
		
		Mono<Comentarios> comentariosUsuario= Mono.fromCallable(()->{
			
			Comentarios comentarios= new Comentarios();
			
			comentarios.addComentario("Hola");
			comentarios.addComentario("mundo");
			comentarios.addComentario("que onda");
			comentarios.addComentario("whaassss");
			comentarios.addComentario("aaaaa");
			
			return comentarios;
		});
		
		
		usuarioMono.flatMap(u -> comentariosUsuario.map(c -> new UsuarioComentario(u,c))  )
		.subscribe(uc->log.info(uc.toString()))
		;
		
		
		
	}
	
	
	
	
	
	
	public void ejemploCollectList() throws Exception {

		List<Usuario> lista = new ArrayList<>();
		lista.add(new Usuario("Pedro"," Guzman"));
		lista.add(new Usuario("Andres", "Bustamante"));
		lista.add(new Usuario("Maria","Hernandez"));
		lista.add(new Usuario("Juan", "Melchor"));
		lista.add(new Usuario("Pepe","Duarte"));
		lista.add(new Usuario("Bruce","Lee"));
		lista.add(new Usuario("Bruce"," Willims"));
		
		Flux.fromIterable(lista)
		//Convierte a un Observable de Mono
		.collectList()
		.subscribe(listaMono->{
			log.info(listaMono.toString());
			
			listaMono.forEach(item->log.info(item.toString()));
			
		
		});
		
		
	
	
	}

	
	public void ejemploToString() throws Exception {

		List<Usuario> lista = new ArrayList<>();
		lista.add(new Usuario("Pedro"," Guzman"));
		lista.add(new Usuario("Andres", "Bustamante"));
		lista.add(new Usuario("Maria","Hernandez"));
		lista.add(new Usuario("Juan", "Melchor"));
		lista.add(new Usuario("Pepe","Duarte"));
		lista.add(new Usuario("Bruce","Lee"));
		lista.add(new Usuario("Bruce"," Willims"));

		// Observable Flux es inmutable
		// Flux<String> nombres= Flux.just("Pedro Guzman","Andres Bustamante","Maria
		// Hernandez","Juan Melchor","Pepe Duarte","Bruce Lee","Bruce Willims");
		Flux.fromIterable(lista).map(usuario -> {

			String nombre = usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido());
 
			return nombre;
		}).flatMap(nombre -> {
			 if(nombre.contains("BRUCE")){
				 return Mono.just(nombre);
			 }else {
				 return Mono.empty();
			 }
			})
		.map(nombre ->nombre.toLowerCase())
				.subscribe(
						// happy path
						nombre -> log.info(nombre)
					

		);

	}
	
	public void flatMap() throws Exception {

		List<String> lista = new ArrayList<>();
		lista.add("Pedro Guzman");
		lista.add("Andres Bustamante");
		lista.add("Maria Hernandez");
		lista.add("Juan Melchor");
		lista.add("Pepe Duarte");
		lista.add("Bruce Lee");
		lista.add("Bruce Willims");

		// Observable es inmutable
		// Flux<String> nombres= Flux.just("Pedro Guzman","Andres Bustamante","Maria
		// Hernandez","Juan Melchor","Pepe Duarte","Bruce Lee","Bruce Willims");
		Flux.fromIterable(lista).map(nombre -> {

			String[] arreglo = nombre.split(" ");

			return new Usuario(arreglo[0].toUpperCase(), arreglo[1]);
		}).flatMap(usuario -> {
			 if(usuario.getNombre().equalsIgnoreCase("bruce")){
				 return Mono.just(usuario);
			 }else {
				 return Mono.empty();
			 }
			})
		.map(usuario -> {
			String nombre = usuario.getNombre().toLowerCase();
			usuario.setNombre(nombre);
			return usuario;
		})
				.subscribe(
						// happy path
						usuario -> log.info(usuario.toString())
					

		);

	}

	public void ejemploIterable() throws Exception {

		List<String> lista = new ArrayList<>();
		lista.add("Pedro Guzman");
		lista.add("Andres Bustamante");
		lista.add("Maria Hernandez");
		lista.add("Juan Melchor");
		lista.add("Pepe Duarte");
		lista.add("Bruce Lee");
		lista.add("Bruce Willims");

		// Observable es inmutable
		// Flux<String> nombres= Flux.just("Pedro Guzman","Andres Bustamante","Maria
		// Hernandez","Juan Melchor","Pepe Duarte","Bruce Lee","Bruce Willims");
		Flux<String> nombres = Flux.fromIterable(lista);

		// Cambiar los valores que tiene adentro, ponerlo en mayusculas, agregar algun
		// proceso y retorna los valores con la modificacion
		// a este punto ya cambio de Observable String a Observable Usuario
		Flux<Usuario> usuarios = nombres.map(nombre -> {

			String[] arreglo = nombre.split(" ");

			return new Usuario(arreglo[0].toUpperCase(), arreglo[1]);
		}).filter(usuario -> usuario.getNombre().equalsIgnoreCase("bruce")).doOnNext(usuario -> {

			if (usuario == null) {

				throw new RuntimeException("Los nombres no pueden estar vacios");
			}

			System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));

		})
				// Cambiar los valores que tiene adentro, ponerlo en mayusculas, agregar algun
				// proceso y retorna los valores con la modificacion
				.map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});
		// .doOnNext(System.out::println);

		// Es como en angular los Observables
		usuarios.subscribe(
				// happy path
				e -> log.info(e.toString()),
				// Error
				error -> log.error(error.getMessage()),
				// hilos
				new Runnable() {

					@Override
					public void run() {
						log.info("Finaliza la ejecución del Observable con exito");

					}
				}

		);

	}

}
