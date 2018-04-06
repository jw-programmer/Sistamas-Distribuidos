package conexoes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import interfaces.CallbackInterface;
import interfaces.CallbackOuvinte;

public class Servidor implements Runnable, CallbackOuvinte {
	private int porta;
	private ServerSocket entrada;
	private List<Ouvinte> ouvintes;
	private CallbackInterface feed;
	private boolean flag;

	public Servidor(int porta, CallbackInterface feed) throws IOException {
		this.porta = porta;
		this.feed = feed;
		this.flag = true;
		this.entrada = new ServerSocket(porta);
		this.ouvintes = new LinkedList<>();
	}

	public Servidor(int porta) throws IOException {
		this.porta = porta;
		this.feed = null;
		this.flag = true;
		this.entrada = new ServerSocket(porta);
		this.ouvintes = new LinkedList<>();
	}

	public void loopServidor() throws IOException {
		while (flag) {
			Socket novo = entrada.accept();
			Ouvinte novoOuvinte = new Ouvinte(novo, this);
			Thread ouvir = new Thread(novoOuvinte);
			ouvintes.add(novoOuvinte);
			ouvir.start();
		}
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public ServerSocket getEntrada() {
		return entrada;
	}

	public void setEntrada(ServerSocket entrada) {
		this.entrada = entrada;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public void run() {
		try {
			loopServidor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			for(Ouvinte o: ouvintes) {
				o.down = false;
			}
			ouvintes = new LinkedList<>();
		}
	}

	@Override
	public void novaMensagem(String msg) {
		feed.atualizarMensagens(msg);
	}
}
