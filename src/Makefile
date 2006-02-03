# Java Calendar Tools Makefile
# $Id$
#
# Notes:
# If you add/remove/change the java files that need to be compiled, just
# do 'make update' to update this Makefile.
#
# This has only been tested on Linux and Windoze/Cygwin, but should work
# with any OS.
#
# And, yes, I know java people like to use Ant... 
#
##############################################################################

JCC =	javac
JAR =	jar
JAVADOC = javadoc
# script to sign the JAR file
SIGNJAR = signjar.sh

VERSION = 0.1.0

PACKAGEDIR = us/k5n/ical
ICALJAR = ../dist/lib/k5n-ical-$(VERSION).jar

.SUFFIXES: .java .class

.java.class:
	$(JCC) -classpath . $*.java

# START CLASSLIST
CLASSLIST = $(PACKAGEDIR)/Constants.class $(PACKAGEDIR)/Event.class $(PACKAGEDIR)/Attendee.class $(PACKAGEDIR)/Attribute.class $(PACKAGEDIR)/Property.class $(PACKAGEDIR)/StringUtils.class $(PACKAGEDIR)/Date.class $(PACKAGEDIR)/Description.class $(PACKAGEDIR)/ParseException.class $(PACKAGEDIR)/Utils.class $(PACKAGEDIR)/Sequence.class $(PACKAGEDIR)/BogusDataException.class $(PACKAGEDIR)/Categories.class $(PACKAGEDIR)/Summary.class $(PACKAGEDIR)/Uid.class $(PACKAGEDIR)/IcalParser.class $(PACKAGEDIR)/Timezone.class $(PACKAGEDIR)/ParseErrorListener.class $(PACKAGEDIR)/ParseError.class $(PACKAGEDIR)/IcalLine.class $(PACKAGEDIR)/Journal.class $(PACKAGEDIR)/DataStore.class $(PACKAGEDIR)/Todo.class $(PACKAGEDIR)/Rrule.class $(PACKAGEDIR)/Freebusy.class $(PACKAGEDIR)/Duration.class $(PACKAGEDIR)/DefaultDataStore.class 
# END CLASSLIST

all: ../dist/lib $(ICALJAR)

../dist/lib: ../dist
	mkdir ../dist/lib

../dist:
	mkdir ../dist

$(ICALJAR): $(CLASSLIST)
	rm -f $(ICALJAR); \
	$(JAR) cf $(ICALJAR) \
	us/k5n/ical/*class
	@if [ -f $(SIGNJAR) ]; then \
		$(SIGNJAR) $(ICALJAR); \
	fi

clean:
	rm -f $(CLASSLIST) webcalendar.jar

update:
	@echo "Updating makefile"; \
	sed -n '1,/^# START CLASSLIST/ p' Makefile > Makefile.new; \
	echo "CLASSLIST = " | tr -d '\012\015' >> Makefile.new; \
	make --silent classlist >> Makefile.new; \
	sed -n '/^# END CLASSLIST/,$$ p' Makefile >> Makefile.new; \
	mv Makefile Makefile.old; \
	mv Makefile.new Makefile


classlist:
	@find $(PACKAGEDIR) -name "*.java" -print | \
	sed 's?$(PACKAGEDIR)?$$(PACKAGEDIR)?g' | \
	sed 's;\.java;.class;g' | tr '\012\015' '  '; \
	echo ""


doc:
	$(JAVADOC) -d api us.k5n.webcalendar us.k5n.webcalendar.ui
