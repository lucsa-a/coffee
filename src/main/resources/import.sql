SHOW TABLES;

-- Endereço
INSERT INTO endereco (id, cep, uf, cidade, bairro, rua, numero, complemento) VALUES (10001, '01001-000', 'PB', 'Areial', 'Centro', 'São José', 1, '');

INSERT INTO endereco (id, cep, uf, cidade, bairro, rua, numero, complemento) VALUES (10002, '20040-020', 'RJ', 'Rio de Janeiro', 'Centro', 'Av. Rio Branco', 50, 'Sala 305');

-- Fornecedor
INSERT INTO fornecedor (id, cod_acesso, nome, cnpj) VALUES (10001, '123456', 'Fornecedor A', '12.345.678/0001-99');

INSERT INTO fornecedor (id, cod_acesso, nome, cnpj) VALUES (10002, '789123', 'Fornecedor B', '98.765.432/0001-88');

INSERT INTO fornecedor (id, cod_acesso, nome, cnpj) VALUES (10003, '098765', 'Fornecedor C', '98.765.432/0001-88');

-- Cliente
INSERT INTO cliente (id, nome, endereco_id, cod_acesso, exclusividade) VALUES (10001, 'sicrano', 10001, '123456', 'PREMIUM');

INSERT INTO cliente (id, nome, endereco_id, cod_acesso, exclusividade) VALUES (10002, 'beltrano', 10002, '654321', 'NORMAL');

INSERT INTO cliente (id, nome, endereco_id, cod_acesso, exclusividade) VALUES (10003, 'joão', 10002, '789012', 'PREMIUM');

-- Café
INSERT INTO cafe (id, nome, origem, tipo, perfil_sensorial, preco, tamanho_embalagem, exclusividade, disponivel, fornecedor_id) VALUES (10001, 'Café A', 'Brasil', 'GRAO', 'Doce e frutado', 29.90, 500.0, 'PREMIUM', true, 10001);

INSERT INTO cafe (id, nome, origem, tipo, perfil_sensorial, preco, tamanho_embalagem, exclusividade, disponivel, fornecedor_id) VALUES (10002, 'Café B', 'Colômbia', 'CAPSULA', 'Amargo e forte', 19.90, 250.0, 'NORMAL', true, 10001);

-- Veículo
INSERT INTO veiculo (id, placa, tipo, cor) VALUES (10001, 'ABC1234', 'CARRO', 'Preto');

INSERT INTO veiculo (id, placa, tipo, cor) VALUES (10002, 'XYZ5678', 'MOTO', 'Vermelho');

-- Entregador
INSERT INTO entregador (id, nome, cod_acesso, veiculo_id) VALUES (10001, 'João Silva', '123456', 10001);

INSERT INTO entregador (id, nome, cod_acesso, veiculo_id) VALUES (10002, 'Maria Oliveira', '654321', 10002);
