package br.usp.ime.academicdevoir.controller;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.usp.ime.academicdevoir.arquivos.Arquivos;
import br.usp.ime.academicdevoir.dao.AlunoDao;
import br.usp.ime.academicdevoir.dao.DisciplinaDao;
import br.usp.ime.academicdevoir.dao.ListaDeExerciciosDao;
import br.usp.ime.academicdevoir.dao.ListaDeRespostasDao;
import br.usp.ime.academicdevoir.dao.QuestaoDao;
import br.usp.ime.academicdevoir.entidade.EstadoDaListaDeRespostas;
import br.usp.ime.academicdevoir.entidade.ListaDeRespostas;
import br.usp.ime.academicdevoir.entidade.Questao;
import br.usp.ime.academicdevoir.entidade.Resposta;
import br.usp.ime.academicdevoir.infra.TipoDeQuestao;
import br.usp.ime.academicdevoir.infra.UsuarioSession;
import br.usp.ime.academicdevoir.infra.VerificadorDePrazos;

@Resource
public class RespostasController {

	private final ListaDeRespostasDao dao;
	//private final ListaDeExerciciosDao listaDeExerciciosDao;
	//private final AlunoDao alunoDao;
	//private final DisciplinaDao disciplinaDao;
	//private final UsuarioSession usuario;
	private final Arquivos arquivos;
	private final Result result;
	//private final Validator validator;
	private final QuestaoDao questaoDao;

	public RespostasController(ListaDeRespostasDao dao,
			ListaDeExerciciosDao listaDeExerciciosDao, AlunoDao alunoDao, DisciplinaDao disciplinaDao,
			UsuarioSession usuario, Arquivos arquivos, Result result, Validator validator, QuestaoDao questaoDao) {
		this.dao = dao;
		//this.listaDeExerciciosDao = listaDeExerciciosDao;
		//this.alunoDao = alunoDao;
		//this.disciplinaDao = disciplinaDao;
		this.questaoDao = questaoDao;
		//this.usuario = usuario;
		this.arquivos = arquivos;
		this.result = result;
		//this.validator = validator;
	}
	
	/**
	 * Salva uma resposta referente na lista de respostas fornecida.
	 * @param listaDeRespostas
	 * @param listaDeExercicios
	 */
	@Post
	@Path("/respostas/{listaDeRespostas.id}/cadastra")
	public void salvaResposta(ListaDeRespostas listaDeRespostas, Resposta resposta, Long idDaQuestao, UploadedFile arquivo) {
	    String caminho;
	    int nenvios, nmaxenvios;
	    if (resposta == null) resposta = new Resposta();
		
	    dao.recarrega(listaDeRespostas);
	    
	    
        if (listaDeRespostas.getPropriedades().getEstado() == 
                EstadoDaListaDeRespostas.SALVA && 
                !VerificadorDePrazos.estaNoPrazo(listaDeRespostas.
                getListaDeExercicios().getPropriedades().getPrazoDeEntrega())) {
            listaDeRespostas.getPropriedades().
                setEstado(EstadoDaListaDeRespostas.FINALIZADA);
            dao.atualiza(listaDeRespostas);
            result.redirectTo(ListasDeExerciciosController.class).
            listasTurma(listaDeRespostas.getListaDeExercicios().
                    getTurma().getId());
            return;
        }
	    
		Questao questao = questaoDao.carrega(idDaQuestao);		
			
		if (questao.getTipo() == TipoDeQuestao.SUBMISSAODEARQUIVO && arquivo != null) {
			arquivos.salva(arquivo, idDaQuestao);
			resposta.setValor(arquivo.getFileName());
		}
		if (questao.getTipo() == TipoDeQuestao.CODIGO) {
		    caminho = arquivos.getPastaDaQuestao(questao.getId()).getAbsolutePath();
		    resposta.setCaminhoParaDiretorioDeTeste(caminho);
		}
		
        resposta.setQuestao(questao);
        
		if (listaDeRespostas.getListaDeExercicios().getPropriedades().
		        getNumeroMaximoDeEnvios() != null) {
	        nmaxenvios =  listaDeRespostas.getListaDeExercicios().getPropriedades().
	        getNumeroMaximoDeEnvios();
		    nenvios = listaDeRespostas.getPropriedades().getNumeroDeEnvios();
		    if (listaDeRespostas.getRespostas().isEmpty())
		        listaDeRespostas.getPropriedades().setNumeroDeEnvios(nenvios + 1);
        
		    if (listaDeRespostas.adiciona(resposta) == 0)
		        listaDeRespostas.getPropriedades().setNumeroDeEnvios(nenvios + 1);
		    if (listaDeRespostas.getPropriedades().getNumeroDeEnvios() >= nmaxenvios)
		        listaDeRespostas.getPropriedades().setEstado
		            (EstadoDaListaDeRespostas.FINALIZADA);
		}
		else
		    listaDeRespostas.adiciona(resposta);
		
		dao.atualiza(listaDeRespostas);
       
	    result.redirectTo(ListasDeExerciciosController.class).
	            listasTurma(listaDeRespostas.getListaDeExercicios().
	                    getTurma().getId());
	}
	
	/**
	 * Remove a lista de respostas fornecida do banco de dados.
	 * @param id
	 */
	@Delete
	@Path("/respostas/{id}")
	public void removeRespostas(Long id) {
		ListaDeRespostas listaDeRespostas = dao.carrega(id);
		dao.remove(listaDeRespostas);		
		/*result.redirectTo;*/
	}
}
