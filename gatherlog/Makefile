#
#

include ../Makedefs

SUBDIRS=generated .
WSDLPATH=../wsdl/
GLWSDLNAME=eucalyptus_gl.wsdl
GLWSDL=$(WSDLPATH)/$(GLWSDLNAME)
SERVICE_SO=libEucalyptusGL.so
SERVICE_NAME=EucalyptusGL
CLIENT=GLclient
#WSDL2C=$(AXIS2C_HOME)/bin/tools/wsdl2c/WSDL2C.sh
#server client
all: generated/stubs
	@for subdir in $(SUBDIRS); do \
		(cd $$subdir && $(MAKE) buildall) || exit $$? ; done

build: all

buildall: server client

generated/stubs: $(GLWSDL)
	rm -rf generated
	mkdir -p generated
	@echo Generating client stubs
	sh $(WSDL2C) -uri $(GLWSDL) -g -d adb -u -f -o generated
	@echo Generating server stubs
	sh $(WSDL2C) -uri $(GLWSDL) -sd -ss -d adb -u -f -o generated
	@echo Adding marshalling calls to server stubs
	@ ../tools/add_marshalling.pl generated/axis2_skel_*.c
	@cp ../tools/Makefile.gen generated/Makefile
	@echo "this is a cute little makefile marker" >generated/stubs
	@echo Stubs generated.

.c.o:
	$(CC) -c $(CFLAGS) $(INCLUDES) $<

server: $(SERVICE_SO)

$(SERVICE_SO): generated/stubs $(GENERATEDOBJS) gl-client-marshal-adb.o server-marshal.o handlers.o
	$(CC) -shared generated/*.o server-marshal.o handlers.o $(WSSECLIBS) $(LIBS) $(LDFLAGS) ../util/misc.o ../storage/diskutil.o ../util/ipc.o ../util/euca_auth.o ./gl-client-marshal-adb.o -o $(SERVICE_SO) -lssl -lcrypto

client: $(CLIENT)

$(CLIENT): generated/stubs $(CLIENT).c gl-client-marshal-adb.c handlers.o
	$(CC) -o $(CLIENT) $(CFLAGS) $(INCLUDES) $(CLIENT).c gl-client-marshal-adb.c generated/adb_*.o generated/axis2_stub_*.o generated/axis2_ext*.o ../util/misc.o ../storage/diskutil.o ../util/ipc.o ../util/euca_auth.o -DMODE=1 $(LIBS) $(LDFLAGS) -lssl -lcrypto

deploy:
	$(INSTALL) -d $(DESTDIR)$(AXIS2C_SERVICES)/$(SERVICE_NAME)/
	$(INSTALL) $(SERVICE_SO) $(DESTDIR)$(AXIS2C_SERVICES)/$(SERVICE_NAME)/
	$(INSTALL) $(GLWSDL) $(DESTDIR)$(AXIS2C_SERVICES)/$(SERVICE_NAME)/
#no ws-sec
	cat generated/services.xml | ../tools/add_wsdllocation.pl $(AXIS2C_SERVICES)/$(SERVICE_NAME)/$(GLWSDLNAME) > $(DESTDIR)$(AXIS2C_SERVICES)/$(SERVICE_NAME)/services_noWS.xml

#ws-sec
#	cat generated/services.xml | ../tools/add_wsdllocation.pl $(AXIS2C_SERVICES)/$(SERVICE_NAME)/$(GLWSDLNAME) | ../tools/add_service_policy.pl ../tools/service-policy-template.xml | sed "s:CLIENT-CERT:cloud-cert.pem:g" | sed "s:CLIENT-KEY:cloud-pk.pem:g" | sed "s:SERVER-CERT:cluster-cert.pem:g" | sed "s:SERVER-KEY:cluster-pk.pem:g" | sed "s:CLIENT-USERNAME:eucalyptus:g" > $(AXIS2C_SERVICES)/$(SERVICE_NAME)/services_WS.xml
#	cat ../tools/client-policy-template.xml | sed "s:EUCALYPTUS_HOME:$(EUCALYPTUS):g" | sed "s:AXIS2C_HOME:$(AXIS2C_HOME):g" | sed "s:CLIENT-CERT:cloud-cert.pem:g" | sed "s:SERVER-CERT:cluster-cert.pem:g" | sed "s:SERVER-KEY:cluster-pk.pem:g" | sed "s:CLIENT-KEY:cloud-pk.pem:g" | sed "s:CLIENT-USERNAME:eucalyptus:g" > cc-client-policy.xml
#	cp ../tools/*.pem $(EUCALYPTUS)/var/lib/eucalyptus/keys/
#	cp cc-client-policy.xml $(EUCALYPTUS)/var/lib/eucalyptus/keys/
#	chmod -R 0600 $(EUCALYPTUS)/var/lib/eucalyptus/keys/*
# ws
#	cp $(AXIS2C_SERVICES)/$(SERVICE_NAME)/services_WS.xml $(AXIS2C_SERVICES)/$(SERVICE_NAME)/services.xml
# no ws
	$(INSTALL) $(DESTDIR)$(AXIS2C_SERVICES)/$(SERVICE_NAME)/services_noWS.xml $(DESTDIR)$(AXIS2C_SERVICES)/$(SERVICE_NAME)/services.xml

clean:
	rm -f $(SERVICE_SO) *.o $(CLIENT) *~* *#*  

distclean:
	rm -rf generated $(SERVICE_SO) *.o $(CLIENT) *~* *#*

install:

uninstall:
	@$(RM) -rf $(DESTDIR)$(AXIS2C_SERVICES)/$(SERVICE_NAME)
