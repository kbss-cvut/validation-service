package cz.cvut.kbss.validation;

import com.github.sgov.server.ValidationRules;
import com.github.sgov.server.Validator;
import cz.cvut.kbss.validation.exception.ValidatorException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.rio.turtle.TurtleWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Vocabulary validation service.
 */
@Singleton
public class ValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationService.class);

    @ConfigProperty(name = "validator.repositoryUrl")
    String repositoryUrl;

    @ConfigProperty(name = "validator.username")
    Optional<String> username;

    @ConfigProperty(name = "validator.password")
    Optional<String> password;

    @ConfigProperty(name = "validator.defaultLanguage", defaultValue = "cs")
    String defaultLanguage;

    private final Validator validator = new Validator();

    private RepositoryManager repositoryManager;

    private Repository repository;

    @PostConstruct
    void init() {
        final String[] repositoryParts = repositoryUrl.split("/repositories/");
        if (repositoryParts.length != 2) {
            throw new IllegalStateException("Expected RDF4J-style repository URL, but was " + repositoryUrl);
        }
        if (username.isPresent() && password.isPresent()) {
            LOG.debug("Connecting to repository '{}' with username '{}' and password.", repositoryUrl, username.get());
            this.repositoryManager =
                    RemoteRepositoryManager.getInstance(repositoryParts[0], username.get(), password.get());
        } else {
            LOG.debug("Connecting to repository '{}'.", repositoryUrl);
            this.repositoryManager = RemoteRepositoryManager.getInstance(repositoryParts[0]);
        }
        repositoryManager.init();
        this.repository = repositoryManager.getRepository(repositoryParts[1]);
        repository.init();
    }

    @PreDestroy
    public void shutDown() {
        repository.shutDown();
        repositoryManager.shutDown();
    }

    /**
     * Validates the specified repository contexts.
     * <p>
     * Presumably, these contexts contain vocabularies to validate. The language parameter specifies language for some
     * of the validation rules and specifies the primary language of the vocabularies.
     *
     * @param contexts Vocabulary contexts to validate
     * @return Validation report
     */
    public ValidationReport validate(Collection<String> contexts, String language) {
        Objects.requireNonNull(contexts);
        if (language == null) {
            language = defaultLanguage;
        }
        LOG.debug("Validating contexts '{}' with language '{}'.", contexts, language);

        final long start = System.currentTimeMillis();
        final Model dataModel = getModelFromRdf4jRepository(contexts);
        final ValidationReport report = validator.validate(dataModel, language);
        dataModel.close();
        final long end = System.currentTimeMillis();
        logFinish(end, start);
        return report;
    }

    private static void logFinish(long end, long start) {
        LOG.trace("Validation finished after {} ms.", end - start);
    }

    /**
     * Validates the specified repository contexts using the specified rules.
     * <p>
     * Presumably, these contexts contain vocabularies to validate. The language parameter specifies language for some
     * of the validation rules and specifies the primary language of the vocabularies.
     * <p>
     * The rules parameter specifies the names of the rules to use. If no such rule is found, it is skipped.
     *
     * @param contexts Vocabulary contexts to validate
     * @return Validation report
     */
    public ValidationReport validateWithRules(Collection<String> contexts, Collection<String> rules, String language) {
        Objects.requireNonNull(contexts);
        Objects.requireNonNull(rules);
        if (language == null) {
            language = defaultLanguage;
        }
        LOG.debug("Validating contexts '{}' with language '{}' using rules {}.", contexts, language, rules);

        final long start = System.currentTimeMillis();
        final Model dataModel = getModelFromRdf4jRepository(contexts);
        final Model ruleModel = loadRuleModel(rules, language);
        final ValidationReport report = validator.validate(dataModel, ruleModel);
        dataModel.close();
        ruleModel.close();
        final long end = System.currentTimeMillis();
        logFinish(end, start);
        return report;
    }

    private Model loadRuleModel(Collection<String> rules, String language) {
        final Model model = ModelFactory.createDefaultModel();
        ValidationRules.rules(language).stream().filter(r -> rules.contains(r.name())).forEach(rule -> {
            final ByteArrayInputStream is = new ByteArrayInputStream(rule.content().getBytes(StandardCharsets.UTF_8));
            model.read(is, null, FileUtils.langTurtle);
        });
        return model;
    }

    private Model getModelFromRdf4jRepository(final Collection<String> contextUris) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        final ValueFactory vf = repository.getValueFactory();
        try (final RepositoryConnection c = repository.getConnection()) {
            final List<IRI> iris = new ArrayList<>();
            contextUris.forEach(i -> iris.add(vf.createIRI(i)));
            c.export(new TurtleWriter(writer), iris.toArray(new IRI[]{}));
            writer.close();
        } catch (IOException e) {
            throw new ValidatorException("Unable to transfer data from repository.", e);
        }
        final byte[] savedData = baos.toByteArray();
        final ByteArrayInputStream is = new ByteArrayInputStream(savedData);
        Model model = ModelFactory.createDefaultModel();
        model.read(is, null, FileUtils.langTurtle);
        return model;
    }
}
