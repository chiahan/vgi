##
## Makefile.am for vgi in /home/loulou/lrde_proj/vgi/src
## 
## Made by Louis-Noel Pouchet
## Login   <loulou@lrde.epita.fr>
## 
## Started on  Wed Mar  2 13:14:16 2005 Louis-Noel Pouchet
## Last update Wed Jun 22 23:09:32 2005 Louis-Noel Pouchet
##


## List sources in PACKAGE_SRC variable.
COMMON_SRC = 					\
AutomatonStructureProperties.java		\
CanvasView.java                                 \
Controller.java                                 \
ControllerInterface.java			\
## CreateStateFrame.java                           \
DrawingObserver.java                            \
## EditorCommands.java				\
## EditorMouseAdapter.java                         \
## EditorToolbar.java				\
EditorToolbar2.java				\
## IO.java                                         \
LabelFactory.java                               \
LayoutTypeConstants.java                        \
MenuBar2.java                                   \
ModeChange.java                                 \
ModeConstants.java                              \
## PopupMenu.java					\
## PropertiesTemplate.java				\
## StateProperties.java				\
## Storage.java					\
StateStructureProperties.java                   \
TransitionProperties.java                       \
Storage2.java					\
TabbedPaneChangeListener.java			\
## TransitionProperties.java			\
## UIEvent.java					\
## UIEventConstants.java                        \
UIEventToUserEvent.java                         \
UIMouseEvent.java                               \
UserEventConstants.java                         \
TabComponenet.java                              \
VGI.java                                        \
XMLFramePopupMenu.java                          


OTHER_SRC = 					\
VGI.java					\
MenuBar2.java					


VGI_SRC = $(COMMON_SRC) MenuBar2.java VGI.java

PACKAGE_SRC = $(COMMON_SRC) $(OTHER_SRC)



## Set distributed files.
EXTRA_DIST = $(PACKAGE_SRC)


## Retrieve values from configure.
JAVAC					= @JAVAC@
JAR					= @JAR@
MV					= mv
BUILD_DIR				= @top_builddir@
ABS_TOP_BUILDDIR			= @abs_top_builddir@

## Programs and informations about them.
CLASSES					= $(PACKAGE_SRC:.java=.class)


## Help make to find files, and understand java suffixes.
VPATH = $(BUILD_DIR)/build/vgi:$(BUILD_DIR)/build/LayoutMaker:$(BUILD_DIR)/build/Editor:$(BUILD_DIR)/lib
SUFFIXES				= .java .class


## Defining classpath. 
CLASSPTH = $(BUILD_DIR)/lib/jgraph.jar:$(BUILD_DIR)/lib/jgraphaddons.jar:$(BUILD_DIR)/lib/xercesImpl.jar:$(BUILD_DIR)/lib/xmlParserAPIs.jar:$(BUILD_DIR)/$(BUILD):$(BUILD_DIR)/lib/vgitoolkit.jar:$(CLASSPATH):.


## Set jar file as source.
all: vgi.jar 
#LayoutMaker.jar Editor.jar


## Build rules.
# Editor.jar: VJToolKit.jar $(CLASSES)
# 	mkdir -p $(BUILD_DIR)/build/Editor $(BUILD_DIR)/bin
# 	$(JAVAC) $(JAVACFLAGS) -classpath $(CLASSPTH)	\
# 	-d $(BUILD_DIR)/build/Editor $(Editor_SRC)
# 	cd $(BUILD_DIR)/build &&					\
# 	$(JAR) cf Editor.jar -C Editor . &&				\
# 	$(MV) Editor.jar ../lib &&					\
# 	 ../src/tools/generate_launch_script.sh $(ABS_TOP_BUILDDIR)	\
# 	bin/Editor $(JAVA) lib/Editor.jar Editor

# LayoutMaker.jar: VJToolKit.jar $(CLASSES)
# 	mkdir -p $(BUILD_DIR)/build/LayoutMaker $(BUILD_DIR)/bin
# 	$(JAVAC) $(JAVACFLAGS) -classpath $(CLASSPTH)	\
# 	-d $(BUILD_DIR)/build/LayoutMaker $(LayoutMaker_SRC)
# 	cd $(BUILD_DIR)/build &&					\
# 	$(JAR) cf LayoutMaker.jar -C LayoutMaker . &&			\
# 	$(MV) LayoutMaker.jar ../lib &&					\
# 	 ../src/tools/generate_launch_script.sh $(ABS_TOP_BUILDDIR)	\
# 	bin/LayoutMaker $(JAVA) lib/LayoutMaker.jar LayoutMaker


vgi.jar: vgitoolkit.jar $(CLASSES)
	mkdir -p $(BUILD_DIR)/build/vgi $(BUILD_DIR)/bin
	$(JAVAC) $(JAVACFLAGS) -classpath $(CLASSPTH)	\
	-d $(BUILD_DIR)/build/vgi $(VGI_SRC)
	cd $(BUILD_DIR)/build &&					\
	$(JAR) cf vgi.jar -C vgi . &&					\
	$(MV) vgi.jar ../lib  &&					\
	 ../src/tools/generate_launch_script.sh $(ABS_TOP_BUILDDIR)	\
	bin/VGI $(JAVA) lib/vgi.jar VGI


vgitoolkit.jar:
	cd ../vgitoolkit && $(MAKE)


## Fake rule. Since Java doen't handle depandancies between files, you have 
## to compile with javac *.java. Although, we need to recompile every time 
## a java file is modified.
.java.class: $<
	@echo -n
