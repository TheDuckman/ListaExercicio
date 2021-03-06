package br.usp.ime.academicdevoir.controller;

import java.util.List;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.usp.ime.academicdevoir.dao.QuestaoDeMultiplaEscolhaDao;
import br.usp.ime.academicdevoir.dao.TagDao;
import br.usp.ime.academicdevoir.entidade.QuestaoDeMultiplaEscolha;
import br.usp.ime.academicdevoir.infra.Permission;
import br.usp.ime.academicdevoir.infra.Privilegio;
import br.usp.ime.academicdevoir.infra.TipoDeQuestao;
import br.usp.ime.academicdevoir.infra.UsuarioSession;

@Permission({ Privilegio.ADMINISTRADOR, Privilegio.PROFESSOR })
@Resource
/**
 * Controlador de questões de múltipla escolha.
 */
public class QuestoesDeMultiplaEscolhaController {

	/**
	 * @uml.property name="dao"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private QuestaoDeMultiplaEscolhaDao dao;
	/**
	 * @uml.property name="result"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private final Result result;
	/**
	 * @uml.property name="validator"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private Validator validator;
	private TagDao tagDao;

	/**
	 * @param result
	 *            para interação com o jsp da questão.
	 * @param validator
	 * @param usuarioSession
	 *            para controle de permissões
	 * @param turmaDao
	 *            para interação com o banco de dados
	 */
	public QuestoesDeMultiplaEscolhaController(QuestaoDeMultiplaEscolhaDao dao,
			TagDao tagDao, Result result, Validator validator,
			UsuarioSession usuarioSession) {
		this.dao = dao;
		this.tagDao = tagDao;
		this.result = result;
		this.validator = validator;
	}

	@Post
	@Path("/questoes/mult")	
	/**
	 * Verifica se a questão de múltipla escolha fornecida é válida e adiciona 
	 * no banco de dados.
	 * @param questao
	 */
	public void cadastra(final QuestaoDeMultiplaEscolha questao,
			List<Integer> resposta, String tags) {

		questao.setTags(tags, tagDao);
		questao.setResposta(resposta);

		validator.validate(questao);
		validator.onErrorUsePageOf(QuestoesController.class).cadastro();

		dao.salva(questao);
		result.redirectTo(this).lista();
	}

	@Get
	@Path("/questoes/mult/{id}")
	/** 
	 * Devolve uma questão de múltipla escolha com o id fornecido.
	 * 
	 * @param id
	 */
	public void alteracao(Long id) {
		QuestaoDeMultiplaEscolha questao = dao.carrega(id);
		result.include("questao", questao);
		result.include("tags", questao.getTagsEmString());
		result.include("numeroDeAlternativas", questao.getAlternativas().size());
	}

	@Put
	@Path("/questoes/mult/{questao.id}")
	/**
	 * Verifica se a questão de múltipla escolha fornecida é válida e atualiza no banco de dados.
	 * 
	 * @param questao
	 */
	public void altera(QuestaoDeMultiplaEscolha questao,
			List<Integer> resposta, String tags) {

		questao.setTags(tags, tagDao);
		questao.setResposta(resposta);

		validator.validate(questao);
		validator.onErrorUsePageOf(this).alteracao(questao.getId());

		dao.atualiza(questao);

		result.redirectTo(this).lista();
	}

	@Delete
	@Path("/questoes/mult/{id}")
	/**
	 * Remove uma questão de múltipla escolha do banco de dados com o id fornecido.
	 * 
	 * @param id
	 */
	public void remove(Long id) {
		QuestaoDeMultiplaEscolha questao = dao.carrega(id);
		dao.remove(questao);
		result.redirectTo(this).lista();
	}

	@Get
	@Path("/questoes/mult")
	/**
	 * Devolve uma lista com todas as questões de múltipla escolha cadastradas 
	 * no banco de dados.
	 */
	public void lista() {
		result.include("tipoDaQuestao", TipoDeQuestao.MULTIPLAESCOLHA);
		result.include("lista", dao.listaTudo());
	}

	public void copia(QuestaoDeMultiplaEscolha questao) {
		dao.salva(questao);
		result.redirectTo(this).alteracao(questao.getId());	
	}
}
