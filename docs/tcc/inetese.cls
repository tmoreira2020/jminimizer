%%%%%
%%
%% Classe ``inetese''
%%
%% Para as disserta��es de mestrado e teses de doutorado
%% do curso de p�s-gradua��o em Ci�ncia da Computa��o 
%% da Universidade Federal de Santa Catarina.
%%
%% Mantida pelo GruTeX (http://www.inf.ufsc.br/grutex)
%%
%% Hist�rico de vers�es:
%% --------------------
%% 0.1 - 26/01/2001 - Augusto Jun Devegili e Luciano Ignaczak
%%       Cria��o do prot�tipo da primeira vers�o
%% 0.5 - 06/04/2001 - Augusto Jun Devegili
%%       Corre��es e libera��o da primeira vers�o `oficial'
%% 0.6 - 11/04/2001 - Augusto Jun Devegili
%%       Inclus�o pacote tocbibind
%%       Altera��es nos ambientes para utilizar o comando \chapter*
%% 0.7 - 26/05/2001 - Ricardo Felipe Cust�dio
%%       Inclus�o dos pacotes babel e graphicx
%%
%% Observa��es:
%% -----------
%%
%% * todos os comandos de usu�rio come�am por `\ine'
%% * todos os comandos internos come�am por `\ine@'
%% * verifique na se��o de pacotes adicionais quais pacotes
%%   precisam estar instalados para a utiliza��o correta
%%   desta classe
%%
%% Exemplo de utiliza��o:
%% ---------------------
%%
%% \documentclass{inetese}
%%
%% \ineTitulo{Aqui vai o titulo da tese}
%% \ineAutor{Aqui vai o nome do autor da tese}
%% \ineOrientador{Aqui vai o nome do orientador do autor}
%% \ineAreaConcentracao{Aqui vai a area de concentra��o}
%% \ineTipoTese{Aqui vai o tipo da tese: disserta��o ou tese}
%% \ineGrau{Aqui vai o grau: mestre ou doutor}
%% \ineMes{Aqui vai o mes da tese}
%% \ineAno{Aqui vai o ano da tese}
%% \ineCoordenadorCurso{Aqui vai o nome do coordenador do CPGCC}
%% \ineMembroBancaA{Aqui vai o nome do 1o. membro da banca}
%% \ineMembroBancaB{Aqui vai o nome do 2o. membro da banca}
%% \ineMembroBancaC{Aqui vai o nome do 3o. membro da banca}
%%
%% \inePaginaDeRosto
%% \inePaginaDeAprovacao
%%
%% \begin{ineEpigrafe}
%%   Aqui vai o texto da epigrafe
%% \end{ineEpigrafe}
%%
%% \begin{ineOferecimento}
%%   Aqui vai o texto de oferecimento
%% \end{ineOferecimento}
%%
%% \begin{ineAgradecimentos}
%%   Aqui vai o texto de agradecimentos
%% \end{ineAgradecimentos}
%%
%% % Insere o Sum�rio
%% \tableofcontents    \clearpage
%%
%% % Insere a lista de Figuras e de Tabelas
%% \listoffigures \clearpage \listoftables \clearpage
%%
%% \begin{ineResumo}
%%   Aqui vai o resumo da tese
%% \end{ineResumo}
%%
%% \begin{abstract}
%%   Aqui vai o `abstract' da tese
%% \end{abstract}
%%
%% -- aqui come�a a monografia
%%
%% \chapter{Introdu��o}
%% \chapter{Revis�o Bibliogr�fica}
%% \chapter{Descri��o do Trabalho Realizado}
%% \chapter{Resultador}
%% \chapter{Conclus�o}
%%
%% % Trocar o nome default Bibliografia para Refer�ncias Bibliogr�ficas
%% \renewcommand\bibname{Refer�ncias Bibliogr�ficas}
%% \bibliographystyle{abnt} % Estilo para gerar refer�ncias em conformidade com
%%                          % as normas brasileiras
%% \bibliography{meu_arquivo_bib}
%%
%% -- aqui termina a monografia
%%%%%

%%%%%
%%
%% Identifica��o da classe
%%
%%%%%
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{inetese} [2001/04/11]

\typeout{}
\typeout{***********************************************}
\typeout{* inetese.cls vers�o 0.7 - 26/05/2001}
\typeout{* Estilo para as disserta��es de mestrado}
\typeout{* e teses de doutorado do Curso de P�s-Gradua��o}
\typeout{* em Ci�ncia da Computa��o da}
\typeout{* Universidade Federal de Santa Catarina}
\typeout{* Apontador: http://www.inf.ufsc.br/grutex}
\typeout{***********************************************}
\typeout{}

%%%%%
%%
%% Declara��o de op��es
%%
%%%%%
\DeclareOption*{\PassOptionToClass{\CurrentOption}{report}}
\ProcessOptions

%%%%%
%%
%% Utiliza��o da classe `report' como base
%%
%%%%%
\LoadClass[12pt,a4paper]{report}

%%%%%
%%
%% Utiliza��o de pacotes adicionais
%%
%%%%%
\RequirePackage{fancyhdr}
\RequirePackage[paper=a4paper,bottom=30mm,left=30mm,right=15mm,top=18mm,headheight=7mm,headsep=5mm,marginparsep=5mm,marginparwidth=10mm]{geometry}
\RequirePackage{times}
\RequirePackage{tocbibind}
\RequirePackage [brazil] {babel}
\RequirePackage{graphicx}
\RequirePackage{indentfirst}
\RequirePackage[small]{caption2}
\RequirePackage{supertabular}

\renewcommand{\captionlabelfont}{\bfseries}
\captionstyle{hang}

\setlength{\belowcaptionskip}{.5cm}

%%%%%
%% 
%% Comandos para formata��o de tamanhos (margens, espa�amento) e cabecalho
%%
%%%%%
\AtBeginDocument{
%  \setlength{\parindent}{3em}
  \setlength{\parindent}{3cm}
  \renewcommand{\baselinestretch}{1.5}
  
  \flushbottom
  
  \pagestyle{fancy}
  \renewcommand{\headrulewidth}{0pt}
  \fancyhead{}
  \fancyhead[R]{\thepage}
  \fancyfoot{}
  
  \fancypagestyle{plain}{%
    \fancyhead{}
    \fancyfoot{}
  }
}

%%%%%
%%
%% Defini��o de variaveis e valores padr�o
%%
%%%%%

%% Armazena o segundo argumento no comando especificado pelo primeiro
\newcommand{\ine@DefineValor}[2]{ \gdef#1{#2} }

\newcommand*{\ine@Titulo}{
  \ClassError{inetese}
    {Faltou especificar o titulo.}
    {Utilize o comando \protect\ineTitulo{} no preambulo.}
}

\newcommand*{\ine@Autor}{
  \ClassError{inetese}
    {Faltou especificar o autor.}
    {Utilize o comando \protect\ineAutor{} no preambulo.}
}

\newcommand*{\ine@Orientador}{
  \ClassError{inetese}
    {Faltou especificar o orientador.}
    {Utilize o comando \protect\ineOrientador{} no preambulo.}
}

\newcommand*{\ine@CoOrientador}{}

\newcommand*{\ine@TipoTese}{
  \ClassError{inetese}
    {Faltou especificar o tipo de tese.}
    {Utilize o comando \protect\ineTipoTese{} no preambulo para
    especificar o tipo de tese: Tese ou Dissertac�o.}
}

\newcommand*{\ine@Grau}{
  \ClassError{inetese}
    {Faltou especificar o grau obtido.}
    {Utilize o comando \protect\ineGrau{} no preambulo para
    especificar o grau obtido: Doutor ou Mestre.}
}

\newcommand*{\ine@AreaConcentracao}{
  \ClassError{inetese}
    {Faltou especificar a area de concentra��o.}
    {Utilize o comando \protect\ineAreaConcentracao{} no preambulo para
    especificar a area de concentra��o (e.g. sistemas de computa��o ou
    sistemas de conhecimento).}
}

\newcommand*{\ine@Mes}{
  \ClassError{inetese}
    {Faltou especificar o mes.}
    {Utilize o comando \protect\ineMes{} no preambulo para
    especificar o mes.}
}

\newcommand*{\ine@Ano}{
  \ClassError{inetese}
    {Faltou especificar o ano.}
    {Utilize o comando \protect\ineAno{} no preambulo para
    especificar o ano.}
}

\newcommand*{\ine@CoordenadorCurso}{
  \ClassError{inetese}
    {Faltou especificar o coordenador do curso.}
    {Utilize o comando \protect\ineCoordenadorCurso{} no preambulo para
    especificar o coordenador do curso.}
}

\newcommand*{\ine@MembroBancaA}{
  \ClassError{inetese}
    {Faltou especificar o primeiro membro da banca.}
    {Utilize o comando \protect\ineMembroBancaA{} no preambulo para
    especificar o primeiro membro da banca.}
}

\newcommand*{\ine@MembroBancaB}{}

\newcommand*{\ine@MembroBancaC}{}

%%%%%
%%
%% Defini��o dos comandos de usuario para informa��es padr�o
%%
%%%%%
\newcommand*{\ineTitulo}[1]{\ine@DefineValor{\ine@Titulo}{#1}}
\newcommand*{\ineAutor}[1]{\ine@DefineValor{\ine@Autor}{#1}}
\newcommand*{\ineOrientador}[1]{\ine@DefineValor{\ine@Orientador}{#1}}
\newcommand*{\ineCoOrientador}[1]{\ine@DefineValor{\ine@CoOrientador}{#1}}
\newcommand*{\ineTipoTese}[1]{\ine@DefineValor{\ine@TipoTese}{#1}}
\newcommand*{\ineGrau}[1]{\ine@DefineValor{\ine@Grau}{#1}}
\newcommand*{\ineAreaConcentracao}[1]{\ine@DefineValor{\ine@AreaConcentracao}{#1}}
\newcommand*{\ineMes}[1]{\ine@DefineValor{\ine@Mes}{#1}}
\newcommand*{\ineAno}[1]{\ine@DefineValor{\ine@Ano}{#1}}
\newcommand*{\ineCoordenadorCurso}[1]{\ine@DefineValor{\ine@CoordenadorCurso}{#1}}
\newcommand*{\ineMembroBancaA}[1]{\ine@DefineValor{\ine@MembroBancaA}{#1}}
\newcommand*{\ineMembroBancaB}[1]{\ine@DefineValor{\ine@MembroBancaB}{#1}}
\newcommand*{\ineMembroBancaC}[1]{\ine@DefineValor{\ine@MembroBancaC}{#1}}

%%%%%
%%
%% Defini��o da P�gina de rosto
%%
%%%%%
\newcommand*{\inePaginaDeRosto}{
  \pagenumbering{roman}
  \thispagestyle{empty}
  \begin{center}
    \fontsize{16pt}{16pt}\selectfont
    \textbf{UNIVERSIDADE FEDERAL DE SANTA CATARINA\\
    CURSO DE BACHARELADO EM CI\^{E}NCIA DA COMPUTA\c{C}\~{A}O}

    \vspace{3cm}

    \fontsize{16pt}{16pt}\selectfont
    \textbf{\ine@Autor}

    \vspace{3cm}

    \fontsize{18pt}{18pt}\selectfont
    \textbf{\ine@Titulo}
  \end{center}

  \vspace{2cm}

  \normalsize \noindent
  Trabalho de Conclus\~{a}o de Curso submetido~ \`{a} ~Universidade ~Federal ~de ~Santa
  Catarina como parte dos requisitos para a obten\c{c}\~{a}o do
  grau de \ine@Grau\ em Ci\^{e}ncia da Computa\c{c}\~{a}o.

  \vspace{2cm}

  \begin{center}
    \fontsize{14pt}{14pt}\selectfont
    Orientador:\\\ine@Orientador

    \vspace{1cm}

    \if*\ine@CoOrientador \else {\ine@CoOrientador}

    \vspace{1cm}

    \normalsize Florian\'{o}polis, \ine@Mes\ de \ine@Ano
  \end{center}

  \vfill
}

%%%%%
%%
%% Defini��o da P�gina de aprova��o
%%
%%%%%
\newcommand*{\ine@Assinatura}[1]{
  \begin{center}
    \normalsize
    \vspace*{2mm}
    \vrule width 10cm height 0.2mm\\
    \vspace{-4mm} #1
  \end{center}
}

\newcommand*{\inePaginaDeAprovacao}{
  \clearpage
  \thispagestyle{empty}
  \begin{center}
    \fontsize{16pt}{16pt}\selectfont
    \textbf{\ine@Titulo}
    \vspace{1cm}
    
    \fontsize{14pt}{14pt}\selectfont
    \ine@Autor
    \vspace{1.5cm}
  \end{center}
    
  \normalsize \noindent
  Esta \ine@TipoTese\ foi julgada adequada para a obten\c{c}\~{a}o
  do t\'{\i}tulo de \ine@Grau\ em Ci\^{e}ncia da Computa\c{c}\~{a}o, 
  e aprovada em sua forma final pela Coordenadoria do Curso de
  Bacharelado em Ci\^{e}ncia da Computa\c{c}\~{a}o.

  \vspace{0.5cm}

  \fontsize{11pt}{11pt}\selectfont

  \ine@Assinatura{\ine@CoordenadorCurso}

  \vspace{0.5cm}

  \normalsize
  \begin{center}
    Banca Examinadora
  \end{center}
  
  \fontsize{11pt}{11pt}\selectfont

  \ine@Assinatura{\ine@Orientador}
  
  \if*\ine@CoOrientador {\ine@Assinatura{\ine@CoOrientador}} \else 

  \ine@Assinatura{\ine@MembroBancaA}

  \ine@Assinatura{\ine@MembroBancaB}

  \ine@Assinatura{\ine@MembroBancaC}

  \normalsize
  \vfill
}

%%%%%
%%
%% P�gina de epigrafe
%%
%%%%%
\newenvironment{ineEpigrafe}{
  \clearpage
  \null\vfill

  \begin{flushright}
    \renewcommand{\baselinestretch}{1}
      \begin{minipage}{10cm}
        \begin{flushright}
          \fontshape{it}\selectfont
}{
        \end{flushright}
    \renewcommand{\baselinestretch}{1.5}
      \end{minipage}
    \end{flushright}
    \newpage
}

%%%%%
%%
%% P�gina de oferecimento
%%
%%%%%
\newenvironment{ineOferecimento}{
  \clearpage
  \null\vfill

  \begin{flushright}
      \begin{minipage}{10cm}
        \begin{flushright}
}{
        \end{flushright}
      \end{minipage}
    \end{flushright}
    \newpage
}

%%%%%
%%
%% P�gina de agradecimentos
%%
%%%%%
\newenvironment{ineAgradecimentos}{
    \chapter*{Agradecimentos}
}{
    \newpage
}

%%%%%
%%
%% P�gina do resumo
%%
%%%%%
\newenvironment{ineResumo}{
  \chapter*{Resumo}
  \addcontentsline{toc}{chapter}{Resumo}
}{
}

%%%%%
%%
%% P�gina do abstract
%%
%%%%%
\renewenvironment{abstract}{
  \chapter*{Abstract}
  \addcontentsline{toc}{chapter}{Abstract}
}{
  \newpage
  \pagenumbering{arabic}
}

%%%%%
%%
%%  Parte de desenvolvimento
%%
%%%%%

\setcounter{secnumdepth}{3}
\renewcommand\thesubsubsection {\thesubsection.\@arabic\c@subsubsection}

%%%%%
%%
%%  Parte de desenvolvimento: Sugest�es de melhoria e corre��es necess�rias
%%
%%%%%
%
% D�vida n�mero 1
% Deve haver uma forma de colocar a primeira letra do \ine@TipoTese em min�sculo
%
% D�vida n�mero 1
% Trocar o \bibname de Bibliografia, o default para portug��s, para Refer�ncias Bibliogr�ficas
% Para contornar o problema est�-se sugerindo colocar o comando abaixo antes de incluir
% o ambiente de bibliografias no corpo da monografia
% \renewcommand\bibname{Refer�ncias Bibliogr�ficas}%
%
% Fim das d�vidas

%%%%% EOF: inetese.cls
