INSERT INTO Application (id, name)
VALUES ('123e4567-e89b-12d3-a456-426655440000', 'Sample Application');

-- Deployment 1: Canary
INSERT INTO Deployment (convertToFull, incrementDelay, incrementPercentage, incrementQuantity, initialPercentage, initialQuantity, shouldIncrement, targetPercentage, targetQuantity, applicationId, kind, name)
VALUES (false, 300000000000, 20, 10, 50, 100, true, 80, 150, '123e4567-e89b-12d3-a456-426655440000', 'quantity', 'Deployment 1'),
       (false, 300000000000, 30, 20, 20, 200, true, 60, 300, '123e4567-e89b-12d3-a456-426655440000', 'manual', 'Deployment 2'),
       (false, 300000000000, 40, 30, 10, 400, true, 40, 600, '123e4567-e89b-12d3-a456-426655440000', 'full', 'Deployment 3');

-- Deployment 1: Canary
INSERT INTO ConfigProp (nullable, propType, deploymentId, name, val)
VALUES (false, 0, 1, 'prop1', '["value1", "value2"]'),
       (true, 1, 1, 'prop2', 'value3'),
       (false, 2, 1, 'prop3', 'value4');

-- Deployment 2: Staging
INSERT INTO ConfigProp (nullable, propType, deploymentId, name, val)
VALUES (false, 3, 2, 'prop4', 'value5'),
       (true, 4, 2, 'prop5', 'value6'),
       (false, 5, 2, 'prop6', 'value7');

-- Deployment 3: Production
INSERT INTO ConfigProp (nullable, propType, deploymentId, name, val)
VALUES (false, 0, 3, 'prop7', '["value8", "value9"]'),
       (true, 1, 3, 'prop8', 'value10'),
       (false, 2, 3, 'prop9', 'value11');
