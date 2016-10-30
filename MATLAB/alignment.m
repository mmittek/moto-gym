init;
%if ~exist('dataLoaded')
    csvFilename = '/Users/mmittek/Desktop/20160930_030744_393_fb.csv';
    data = loadCSVAndPreprocess(csvFilename);
   % data = data(2204:end,:);
    data(:,1) = data(:,1) - data(1,1);
    
    acc = data(:,[ACCX,ACCY,ACCZ]);
    gyro = data(:,[GYROX,GYROY,GYROZ]);
%end

box = makeBox(0.5,1,4);
angle = zeros( size(data,1),3 );
grav = zeros( size(data,1),3 );
orient_gyro = zeros( size(data,1),3 );
orient_acc = zeros( size(data,1),3 );
mag = zeros( size(data,1),3 );
alpha = 0.99;
alpha_mag = 0.9;

for i=2:size(data,1)
    dt = (data(i,1)-data(i-1,1));
    
        beta = alpha - dt/1000;
        grav(i,:) = grav(i-1,:)*beta + (1-beta)*acc(i,:);
        grav(i,:) = grav(i,:)./norm( grav(i,:) );
        orient_acc(i,:) = grav(i,:).*[-1,1,-1];
        angle(i,:) = angle(i-1,:) + gyro(i,:)*dt/1000;
        
        beta_mag = alpha_mag - dt/1000;

        mag(i,:) = mag(i-1,:)*beta_mag + (1-beta_mag)*data(i,[MAGX,MAGY,MAGZ]);
%        mag(i,:) = mag(i,:) ./ norm(mag(i,:));
        mag(i,:) = mag(i,:).*[1,1,-1] ./ norm(mag(i,:));
        
        R = rotXYZ( angle(i,1), angle(i,2), angle(i,3) );
        orient_gyro(i,:) = [R(1,2) ,R(2,2),R(3,2)];
        
end

linacc = acc - grav;

figure(2)
for i=1:10:size(data,1)

    
%     rMag = zeros(4,4);
%     rMag(4,4)=1;
%     rMag(1:3,1:3) = diag( mag(i,:) );
    
    H = diag([mag(i,:) 1]);
    R = rotXYZ( angle(i,1), angle(i,2), angle(i,3) );
    boxr = (R*box')';
    
    
    orient_gyro(i,:) = orient_gyro(i,:);
    orient_acc(i,:) = orient_acc(i,:);
    
    plot(1,1);
    plotVec3D( orient_gyro(i,1) ,orient_gyro(i,2),orient_gyro(i,3));
    plotVec3D( orient_acc(i,1) , orient_acc(i,2) , orient_acc(i,3) );
%    plotVec3D( mag(i,1) , mag(i,2) , mag(i,3));
    hold on;
    plot3( boxr(:,1), boxr(:,2), boxr(:,3), 'b-o' );

%plotVec3D( orient_gyro(i,1)-grav(i,1) ,orient_gyro(i,2)-grav(i,2),orient_gyro(i,3)-grav(i,3));

    hold off;
    view(3);
    axis equal;
    axis([-1,1,-1,1,-1,1]);
    grid on;
    drawnow;

end

