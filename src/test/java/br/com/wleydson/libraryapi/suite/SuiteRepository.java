package br.com.wleydson.libraryapi.suite;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SuiteDisplayName("Suite Test Controller")
@SelectPackages("br.com.wleydson.libraryapi.model.repository")
public class SuiteRepository {

}
