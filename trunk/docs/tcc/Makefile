CC=pdflatex
PROJECT=tcc

all: bbl pdf



bbl:	pdf
	$(CC) $(PROJECT).tex
	bibtex $(PROJECT)
	$(CC) $(PROJECT).tex
	
pdf:	$(PROJECT).tex
	$(CC) $(PROJECT).tex
	$(CC) $(PROJECT).tex

clean:
	rm -f *.aux
	rm -f *.dvi
	rm -f *.log
	rm -f *.blg
	rm -f *.bbl
	rm -f ./figs/*.bak
	rm -f *.toc
	rm -f *.lof
	rm -f *.lot
	rm -f *~
