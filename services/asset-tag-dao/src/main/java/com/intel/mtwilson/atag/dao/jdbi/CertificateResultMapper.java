/*
 * Copyright (C) 2013 Intel Corporation
 * All rights reserved.
 */
package com.intel.mtwilson.atag.dao.jdbi;

import com.intel.mtwilson.atag.model.Certificate;
import com.intel.mtwilson.atag.model.CertificateRequest;
import com.intel.dcsg.cpg.crypto.Sha1Digest;
import com.intel.dcsg.cpg.crypto.Sha256Digest;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.intel.dcsg.cpg.io.UUID;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * 
 * @author jbuhacoff
 */
public class CertificateResultMapper implements ResultSetMapper<Certificate> {

    @Override
    public Certificate map(int i, ResultSet rs, StatementContext sc) throws SQLException {
//        UUID uuid = UUID.valueOf(rs.getBytes("uuid")); // use this when uuid is a binary type in database
        UUID uuid = UUID.valueOf(rs.getString("uuid")); // use this when uuid is a char type in database
        byte[] content = rs.getBytes("certificate");
        Sha1Digest sha1 = Sha1Digest.valueOfHex(rs.getString("sha1"));
        Sha256Digest sha256 = Sha256Digest.valueOfHex(rs.getString("sha256"));
//        Sha1Digest pcrEvent = Sha1Digest.valueOfHex(rs.getString("pcrEvent"));
        Certificate certificate = new Certificate();
        certificate.setId(rs.getLong("id"));
        certificate.setUuid( uuid);
        certificate.setCertificate(content);
        certificate.setSha1(sha1);
        certificate.setSha256(sha256);
//        certificate.setPcrEvent(pcrEvent);
        certificate.setSubject(rs.getString("subject"));
        certificate.setIssuer(rs.getString("issuer"));
        certificate.setNotBefore(rs.getDate("notBefore"));
        certificate.setNotAfter(rs.getDate("notAfter"));
        certificate.setRevoked(rs.getBoolean("revoked"));
        return certificate;
    }
    
}